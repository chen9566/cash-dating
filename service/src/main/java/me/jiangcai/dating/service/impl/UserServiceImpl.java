package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.LoginToken;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.CashWeixinUserDetail;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.LoginTokenRepository;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.util.WeixinAuthentication;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    private final SecurityContextRepository httpSessionSecurityContextRepository
            = new HttpSessionSecurityContextRepository();
    @Autowired
    private LoginTokenRepository loginTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private SubBranchBankRepository subBranchBankRepository;

    @Override
    public boolean mobileRequired(String openId) {
        User user = userRepository.findByOpenId(openId);
        return user == null || StringUtils.isEmpty(user.getMobileNumber());
    }

    @Override
    public boolean bankAccountRequired(String openId) {
        User user = userRepository.findByOpenId(openId);
        if (user == null)
            return true;
        return user.getCards() == null || user.getCards().isEmpty();
    }

    @Override
    public User registerMobile(HttpServletRequest request, String openId, String mobileNumber, String verificationCode
            , String inviteCode)
            throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobileNumber, verificationCode, VerificationType.register);

        User user = userRepository.findByOpenId(openId);
        if (user == null) {
            user = newUser(openId, request);
        }
        if (inviteCode != null) {
            User from = userRepository.findByInviteCode(inviteCode);
            applyGuide(user, from);
        }
        user.setMobileNumber(mobileNumber);
        return userRepository.save(user);
    }

    @Override
    public Card addCard(String openId, String name, String number, Bank bank, Address address, String subBranch) {
//        verificationCodeService.verify(mobile, code, VerificationType.card);

        User user = userRepository.findByOpenId(openId);

        SubBranchBank branchBank = subBranchBankRepository.getOne(subBranch);

        Card card = new Card();
        card.setNumber(number);
        card.setOwner(name);
        card.setBank(branchBank.getBank());

        Address address1 = new Address();
        address1.setCity(PayResourceService.cityById(branchBank.getCityCode()));
        address1.setProvince(PayResourceService.provinceByCity(address1.getCity()));

        card.setAddress(address1);

        card.setSubBranchBank(branchBank);
        card.setSubBranch(branchBank.getName());
//        card.setSubBranch(subBranch);

        if (user.getCards() == null) {
            user.setCards(new ArrayList<>());
        }
        user.getCards().add(card);
        user = userRepository.save(user);
        return user.getCards().stream()
                .filter(card1 -> card1.getNumber().equals(number))
                .findAny()
                .orElseThrow(IllegalStateException::new);
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

        final WeixinAuthentication authentication = new WeixinAuthentication(user);
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

}
