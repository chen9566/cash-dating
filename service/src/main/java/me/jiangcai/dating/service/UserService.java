package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CJ
 */
public interface UserService {

    /**
     * @param openId
     * @return 这个用户需要输入手机号码
     */
    boolean mobileRequired(String openId);

    /**
     * @param openId
     * @return 这个用户需要绑定银行卡
     */
    boolean bankAccountRequired(String openId);

    /**
     * 绑定手机号码
     *
     * @param openId       openId
     * @param mobileNumber 手机
     * @param code         验证码
     * @param inviteCode   邀请码
     * @return 用户实例
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    User registerMobile(String openId, String mobileNumber, String code, String inviteCode)
            throws IllegalVerificationCodeException;

    /**
     * 以这个用户身份登录
     *
     * @param request
     * @param response
     * @param user
     */
    void loginAs(HttpServletRequest request, HttpServletResponse response, User user);

    /**
     * 增加银行卡
     *
     * @param openId   openId
     * @param name     持卡人
     * @param number   卡号
     * @param bankCode 银行代码
     * @param mobile   手机号码（银行预留）
     * @param code     验证码
     * @return 新增加的银行
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    Card addCard(String openId, String name, String number, String bankCode, String mobile, String code)
            throws IllegalVerificationCodeException;

    /**
     * @param openId openId
     * @return 呵呵
     */
    @Transactional(readOnly = true)
    User byOpenId(String openId);

    @Transactional(readOnly = true)
    User byMobile(String mobile);
}
