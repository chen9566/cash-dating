package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * @param verificationCode         验证码
     * @param inviteCode   邀请码
     * @return 用户实例
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    User registerMobile(String openId, String mobileNumber, String verificationCode, String inviteCode)
            throws IllegalVerificationCodeException;

    /**
     * 以这个用户身份登录
     *
     * @param request
     * @param response
     * @param user
     */
    void loginAs(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException;

    /**
     * 增加银行卡
     *
     * @param openId   openId
     * @param name     持卡人
     * @param number   卡号
     * @param bank
     *@param address
     * @param subBranch @return 新增加的银行
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    Card addCard(String openId, String name, String number, Bank bank, Address address, String subBranch)
            throws IllegalVerificationCodeException;

    /**
     * @param openId openId
     * @return 呵呵
     */
    @Transactional(readOnly = true)
    User byOpenId(String openId);

    @Transactional(readOnly = true)
    User byMobile(String mobile);
    @Transactional(readOnly = true)
    User byInviteCode(String code);

    /**
     * 更新用户微信详情
     *
     * @param detail 详情
     * @return 用户实例
     */
    @Transactional
    User updateWeixinDetail(WeixinUserDetail detail);

    /**
     * 新增一个用户
     * @param openId openId 必须的
     * @return 新增的用户
     */
    @Transactional
    User newUser(String openId);
}
