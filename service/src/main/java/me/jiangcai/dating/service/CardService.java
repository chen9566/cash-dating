package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
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
     * @param openId    openId
     * @param name      持卡人
     * @param number    卡号
     * @param bank
     * @param address
     * @param subBranch 支行,可以是code也可以是name
     * @return 新增加的银行
     * @throws IllegalVerificationCodeException 验证码无效
     */
    @Transactional
    Card addCard(String openId, String name, String number, Bank bank, Address address, String subBranch);

    /**
     * 清空银行卡
     *
     * @param openId openId
     */
    @Transactional
    void deleteCards(String openId);
}
