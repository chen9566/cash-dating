package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.LoginToken;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.supplier.Pay123Card;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.InviteUser;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
     * 绑定手机号码
     *
     * @param request
     * @param openId           openId
     * @param mobileNumber     手机
     * @param verificationCode 验证码
     * @param inviteCode       邀请码
     * @return 用户实例
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    User registerMobile(HttpServletRequest request, String openId, String mobileNumber, String verificationCode, String inviteCode)
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
     * @param openId openId
     * @return null or User
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
     * @param detail  详情
     * @param request
     * @return 用户实例
     */
    @Transactional
    User updateWeixinDetail(WeixinUserDetail detail, HttpServletRequest request);

    /**
     * 新增一个用户
     *
     * @param openId  openId 必须的
     * @param request 当前的http请求,我们需要决定这个用户的一些细节,当然这是可选的
     * @return 新增的用户
     */
    @Transactional
    User newUser(String openId, HttpServletRequest request);


    /**
     * 非微信客户端请求登录
     *
     * @param request 当前请求
     * @return 准备许可证
     */
    @Transactional
    LoginToken requestLogin(HttpServletRequest request);

    /**
     * 用户user许可了id的登录
     *
     * @param id
     * @param user
     */
    @Transactional
    void approvalLogin(long id, User user);

    /**
     * 检查之前的登录许可是否已被处理,如果已被处理直接完成登录
     *
     * @param id
     * @param request  request
     * @param response response
     * @throws ServletException
     * @throws IOException
     * @throws IllegalStateException    还没被许可,但有可能
     * @throws IllegalArgumentException 永远无法许可了。
     */
    @Transactional
    void checkRequestLogin(long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;


    @Transactional(readOnly = true)
    User by(Long id);

    /**
     * 更新密码
     *
     * @param user        用户
     * @param rawPassword 明文密码
     */
    @Transactional
    void updatePassword(User user, String rawPassword);

    /**
     * 检查这个用户的卡,如果用户尚未持有则将分配一个给他,并且在5分钟后提示客服检查新的卡
     *
     * @param openId
     * @return 新配到的卡, null 如果可用的台卡不足;需要马上发送消息给客服
     */
    @Transactional
    Pay123Card updatePay123Card(String openId);

    /**
     * @param openId 用户的openId
     * @return 有效的邀请数量（此处有效指的是已经激活并且使用过刷卡的）
     */
    @Transactional(readOnly = true)
    long validInvites(String openId);

    /**
     * @param openId 用户的openId
     * @return 已经激活并且使用过刷卡的邀请用户
     */
    @Transactional(readOnly = true)
    List<InviteUser> validInviteUsers(String openId);

    /**
     * @param openId
     * @return 用户是否为有效（即刷卡过的）
     */
    @Transactional(readOnly = true)
    boolean isValidUser(String openId);

    /**
     * @param openId 用户的openId
     * @return 所有的邀请用户
     */
    @Transactional(readOnly = true)
    List<InviteUser> allInviteUsers(String openId);
}
