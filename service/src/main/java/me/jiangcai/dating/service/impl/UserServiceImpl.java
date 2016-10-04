package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.util.WeixinAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;

    private final SecurityContextRepository httpSessionSecurityContextRepository
            = new HttpSessionSecurityContextRepository();

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
    public User registerMobile(String openId, String mobileNumber, String code, String inviteCode)
            throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobileNumber, code, VerificationType.register);

        User user = userRepository.findByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
        }
        user.setMobileNumber(mobileNumber);
        return userRepository.save(user);
    }

    @Override
    public Card addCard(String openId, String name, String number, String bankCode, String mobile, String code)
            throws IllegalVerificationCodeException {
        verificationCodeService.verify(mobile, code, VerificationType.card);
        User user = userRepository.findByOpenId(openId);
        Card card = new Card();
        card.setNumber(number);
        card.setOwner(name);
        card.setType(bankCode);
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
    public void loginAs(HttpServletRequest request, HttpServletResponse response, User user) {
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = httpSessionSecurityContextRepository.loadContext(holder);

        context.setAuthentication(new WeixinAuthentication(user));

        httpSessionSecurityContextRepository.saveContext(context, holder.getRequest(), holder.getResponse());
    }


    @Override
    public User byOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Override
    public User byMobile(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }
}
