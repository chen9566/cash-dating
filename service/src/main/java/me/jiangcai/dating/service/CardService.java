package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收款帐号服务
 *
 * @author CJ
 */
public interface CardService {

    /**
     * @param openId
     * @return 这个用户需要绑定银行卡
     */
    @Transactional(readOnly = true)
    boolean bankAccountRequired(String openId);

    /**
     * 增加银行卡
     *
     * @param openId    openId,如果为空,表示自己维护这个card
     * @param name      持卡人
     * @param id        持卡人身份证号码
     * @param number    卡号
     * @param bank
     * @param address
     * @param subBranch 支行,可以是code也可以是name     @return 新增加的银行
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    Card addCard(String openId, String name, String id, String number, Bank bank, Address address, String subBranch);

    /**
     * 清空银行卡
     *
     * @param openId openId
     */
    @Transactional
    void deleteCards(String openId);

    /**
     * 这个提现订单所用的现金银行卡
     *
     * @param order 提现订单
     * @return null 表示无卡可用
     */
    @Transactional(readOnly = true)
    Card recommend(UserOrder order);

    /**
     * 这个用户默认的收款帐号
     *
     * @param user 用户
     * @return null 表示无卡可用
     */
    @Transactional(readOnly = true)
    Card recommend(User user);

    /**
     * 禁用默认的收款帐号,没有就算了
     *
     * @param openId 用户
     */
    @Transactional
    void disableRecommendCard(String openId);
}
