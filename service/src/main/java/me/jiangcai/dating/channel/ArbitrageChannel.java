package me.jiangcai.dating.channel;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
public interface ArbitrageChannel {

    /**
     * 如果结果是true表示用户需要支付签预先绑定借记卡
     *
     * @return 是否使用一个订单同时作用于支付和提现
     */
    boolean useOneOrderForPayAndArbitrage();

    /**
     * 如果不可以管理则需要给予警告
     *
     * @return 是否可以管理旗下的借记卡
     */
    boolean debitCardManageable();

    /**
     * 建立一份实际支付订单
     *
     * @param order 系统订单
     * @return 平台订单
     */
    PlatformOrder newOrder(CashOrder order) throws IOException, SignatureException;

}
