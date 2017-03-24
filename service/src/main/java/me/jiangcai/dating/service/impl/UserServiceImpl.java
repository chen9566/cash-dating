package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.LoginToken;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserPaymentExtend;
import me.jiangcai.dating.entity.supplier.Pay123Card;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.CashWeixinUserDetail;
import me.jiangcai.dating.model.InviteLevel;
import me.jiangcai.dating.model.InviteUser;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.LoginTokenRepository;
import me.jiangcai.dating.repository.UserAgentInfoRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.repository.supplier.Pay123CardRepository;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.util.WeixinAuthentication;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    private final SecurityContextRepository httpSessionSecurityContextRepository
            = new HttpSessionSecurityContextRepository();
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoginTokenRepository loginTokenRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserAgentInfoRepository userAgentInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private ScheduledFuture<?> lastScheduledFuture;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Pay123CardRepository pay123CardRepository;
    @Autowired
    private TaskScheduler taskScheduler;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Override
    public boolean mobileRequired(String openId) {
        User user = userRepository.findByOpenId(openId);
        return user == null || StringUtils.isEmpty(user.getMobileNumber());
    }

    @Override
    public User registerMobile(HttpServletRequest request, String openId, String mobileNumber, String verificationCode
            , String inviteCode)
            throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobileNumber, verificationCode, VerificationType.register);

        User user = userRepository.findByOpenId(openId);
        if (user == null) {
            user = byMobile(mobileNumber);
            if (user == null)
                user = newUser(openId, request);
        }
        if (inviteCode != null) {
            User from = userRepository.findByInviteCode(inviteCode);
            applyGuide(user, from);
        }

        // 在使用微信之前 已经使用了手机号码注册；那么这个新建的帐号将被删除

        User mobileUser = byMobile(mobileNumber);
        if (mobileUser != null && mobileUser.getOpenId() == null) {
            // 删除  user
            mergeUserTo(user, mobileUser);
            user = mobileUser;
        } else
            user.setMobileNumber(mobileNumber);

        if (user.getAgentUser() != null) {
            applicationEventPublisher.publishEvent(
                    new Notification(user.getAgentUser(), NotifyType.memberRegister, null, null, user.getNickname()
                            , user.getMobileNumber(), user.getJoinTime()));
        }

        return userRepository.save(user);
    }

    /**
     * 合并用户
     *
     * @param from 这里
     * @param to   那里。。
     */
    private void mergeUserTo(User from, User to) {
        to.setEnabled(from.isEnabled());
        to.setAccessTimeToExpire(from.getAccessTimeToExpire());
        to.setAccessToken(from.getAccessToken());
        to.setAgentInfo(from.getAgentInfo());
        to.setCards(from.getCards());
//        if (to.getCards() != null){
//            to.getCards().forEach(card -> {
//                card.set
//            });
//        }
        to.setCity(from.getCity());
        to.setCountry(from.getCountry());
        to.setGender(from.getGender());
//        to.setHeadImageUrl(from.getHeadImageUrl());
        userRepository.delete(from);
        userRepository.save(to);
    }

    @Override
    public void loginAs(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException
            , IOException {
        final WeixinAuthentication authentication = login(request, response, user);

        new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
    }

    private WeixinAuthentication login(HttpServletRequest request, HttpServletResponse response, User user) {
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = httpSessionSecurityContextRepository.loadContext(holder);

        final WeixinAuthentication authentication = new WeixinAuthentication(user, this);
        context.setAuthentication(authentication);
//
        SecurityContextHolder.getContext().setAuthentication(authentication);

        httpSessionSecurityContextRepository.saveContext(context, holder.getRequest(), holder.getResponse());
        return authentication;
    }

    @Override
    public User byOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Override
    public User byMobile(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }

    @Override
    public User byInviteCode(String code) {
        return userRepository.findByInviteCode(code);
    }

    @Override
    public User updateWeixinDetail(WeixinUserDetail detail, HttpServletRequest request) {
        User user = byOpenId(detail.getOpenId());
        if (user != null) {
            user.setLoginTime(LocalDateTime.now());
        }

        // 自己玩自己?
        if (detail instanceof CashWeixinUserDetail)
            return byOpenId(detail.getOpenId());

        if (user == null) {
            user = newUser(detail.getOpenId(), request);
        }
        user.updateWeixinUserDetail(detail);
        return userRepository.save(user);
    }

    @Override
    public User newUser(String openId, HttpServletRequest request) {
        User user = new User();
        user.setOpenId(openId);
        user.setJoinTime(LocalDateTime.now());
        do {
            user.setInviteCode(RandomStringUtils.randomAlphanumeric(7));
        } while (byInviteCode(user.getInviteCode()) != null);

        //要搞脑子了
        if (request != null) {
            Long guideId = CashFilter.inviteBy(request);
            if (guideId != null) {
                final User fromUser = userRepository.findOne(guideId);
                applyGuide(user, fromUser);
            }
        }


        return userRepository.save(user);
    }

    /**
     * 应用邀请者
     *
     * @param user     被邀请的用户
     * @param fromUser 邀请者
     */
    private void applyGuide(User user, User fromUser) {
        if (fromUser != null) {
            log.info(user.getOpenId() + " is invited from " + fromUser.getOpenId());
            user.setGuideUser(fromUser);
            if (user.getGuideUser().getAgentInfo() != null) {
                // 邀请者是一个合伙人
                user.setAgentUser(user.getGuideUser());
            } else
                //如果不是的话 就送给邀请者所在的合伙人
                user.setAgentUser(user.getGuideUser().getAgentUser());

            // user 作为一个可能存在的用户 可能存在着之前的信息 应该被清理
            if (user.getMyAgentInfo() != null)
                userAgentInfoRepository.delete(user.getMyAgentInfo());
        }
    }

    @Override
    public LoginToken requestLogin(HttpServletRequest request) {
        LoginToken token = new LoginToken();
        token.setCreatedTime(LocalDateTime.now());
        return loginTokenRepository.save(token);
    }

    @Override
    public void approvalLogin(long id, User user) {
        loginTokenRepository.getOne(id).setApproval(user);
    }

    @Override
    public void checkRequestLogin(long id, HttpServletRequest request, HttpServletResponse response)
            throws ServletException
            , IOException {
        LoginToken token = loginTokenRepository.findOne(id);
        if (token == null)
            throw new IllegalArgumentException();
        if (LocalDateTime.now().isAfter(token.getCreatedTime().plusMinutes(5))) {
            loginTokenRepository.delete(token);
            throw new IllegalArgumentException();
        }

        if (token.getApproval() != null) {
            // 已经被许可了,准备登录 以及删除
            login(request, response, token.getApproval());
            loginTokenRepository.delete(token);
        } else
            throw new IllegalStateException();
    }

    @Override
    public User by(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
    }

    private Stream<User> allWaiters() {
        return userRepository.findByEnabledTrueAndManageStatus(ManageStatus.waiter).stream()
                .filter(user -> user.getOpenId() != null);
    }

    @Override
    public Pay123Card updatePay123Card(String openId) {
        User user = byOpenId(openId);
        if (user.getUserPaymentExtend() != null && user.getUserPaymentExtend().getPay123Card() != null)
            return user.getUserPaymentExtend().getPay123Card();
        if (user.getUserPaymentExtend() == null) {
            user.setUserPaymentExtend(new UserPaymentExtend());
            user.getUserPaymentExtend().setId(user.getId());
        }
        user.getUserPaymentExtend().setPay123Card(pay123CardRepository.findAllUnused().stream()
                .max((o1, o2) -> new Random().nextInt()).orElse(null));
        if (user.getUserPaymentExtend().getPay123Card() == null) {
            try {
                allWaiters().forEach(waiter -> {
                    Notification notification = new Notification(waiter, NotifyType.notEnoughPay123, null, "notEnoughPay123");
                    applicationEventPublisher.publishEvent(notification);
                });
            } catch (Throwable ignored) {
            }
            return null;
        }

        user.getUserPaymentExtend().setPay123AssignTime(LocalDateTime.now());
        // 5分钟后提醒
        if (lastScheduledFuture == null || lastScheduledFuture.isDone())
            lastScheduledFuture = taskScheduler.scheduleWithFixedDelay(()
                    -> allWaiters().forEach(waiter
                    -> {
                Notification notification = new Notification(waiter, NotifyType.pay123CheckRequired, null, "pay123CheckRequired");
                applicationEventPublisher.publishEvent(notification);
            }), 5 * 60 * 1000);

        return user.getUserPaymentExtend().getPay123Card();
    }

    @Override
    public long validInvites(String openId) {
        StringBuilder hql = new StringBuilder("select count(targetUser) ");
        Query query = fromValidInvites(hql, openId);
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
//        Root<User> userRoot = criteriaQuery.from(User.class);
//        validInvite(openId, criteriaBuilder, criteriaQuery, userRoot);
//        criteriaQuery.select(criteriaBuilder.count(userRoot));
//
//        //
//        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);

        try {
            return (long) query.getSingleResult();
        } catch (NoResultException ignored) {
            return 0;
        }
    }

    @Override
    public InviteLevel inviteLevel(String openId) {
        long x = validInvites(openId);
        if (x < 5)
            return InviteLevel.threshold;
        if (x < 10)
            return InviteLevel.senior;
        if (x < 15)
            return InviteLevel.expert;
        return InviteLevel.best;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InviteUser> allInviteUsers(String openId) {
        Query query = entityManager.createQuery("select new me.jiangcai.dating.model.InviteUser(targetUser.nickname,targetUser.headImageUrl,(select count(o) from CashOrder as o where o.owner=targetUser and o.completed = true) ) " + "from User as targetUser where targetUser.guideUser.openId = :openId ");
        query = query.setParameter("openId", openId);
        return query.getResultList();
    }

    private Query fromValidInvites(StringBuilder hql, String openId) {
        hql.append("from User as targetUser where targetUser.guideUser.openId = :openId " +
                "and (select count(o) from CashOrder as o where o.owner=targetUser and o.completed = true) >= 1");
        Query query = entityManager.createQuery(hql.toString());
        query = query.setParameter("openId", openId);
        return query;
    }

    private void validInvite(String openId, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery
            , Root<User> userRoot) {
//        Root<CashOrder> cashOrderRoot = criteriaQuery.from(CashOrder.class);
        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<CashOrder> cashOrderRoot = subquery.from(CashOrder.class);
        Predicate fromOpenPredicate = criteriaBuilder.equal(userRoot.get("guideUser").get("openId"), openId);
        Predicate ourOrder = criteriaBuilder.equal(cashOrderRoot.get("owner"), subquery.correlate(userRoot));
        Predicate validOrder = criteriaBuilder.isTrue(cashOrderRoot.get("completed"));
        subquery = subquery.where(ourOrder, validOrder, criteriaBuilder.notEqual(subquery.correlate(userRoot).get("openId"), openId));
        subquery = subquery.select(criteriaBuilder.count(cashOrderRoot));
//        Predicate enoughOrders = criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.count(cashOrderRoot), 1L);
//        criteriaQuery.having(enoughOrders);
        criteriaQuery.where(fromOpenPredicate, criteriaBuilder.greaterThanOrEqualTo(subquery.getSelection(), 1L));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InviteUser> validInviteUsers(String openId) {
        StringBuilder hql = new StringBuilder("select new me.jiangcai.dating.model.InviteUser(targetUser.nickname,targetUser.headImageUrl,true) ");
        Query query = fromValidInvites(hql, openId);

//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<InviteUser> criteriaQuery = criteriaBuilder.createQuery(InviteUser.class);
//        Root<User> userRoot = criteriaQuery.from(User.class);
//        Path<String> nickname = userRoot.get("nickname");
//        Path<String> headImageUrl = userRoot.get("headImageUrl");
//        validInvite(openId, criteriaBuilder, criteriaQuery, userRoot);
//        criteriaQuery.select(criteriaBuilder.construct(InviteUser.class, nickname, headImageUrl
//                , criteriaBuilder.literal(true)));
//        TypedQuery<InviteUser> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public boolean isValidUser(String openId) {
        return cashOrderRepository.countByOwner_OpenIdAndCompletedTrue(openId) >= 1;
    }


}
