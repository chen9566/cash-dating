package me.jiangcai.dating.service;

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

}
