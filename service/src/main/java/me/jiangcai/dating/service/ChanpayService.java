package me.jiangcai.dating.service;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.channel.PayChannel;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.event.MyTradeEvent;
import me.jiangcai.dating.event.MyWithdrawalEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.SignatureException;

/**
 * 到畅捷支付的服务
 *
 * @author CJ
 */
public interface ChanpayService extends ArbitrageChannel, PayChannel {

//    /**
//     * 建立支付订单
//     *
//     * @param order 对应订单
//     * @return 已建立的支付订单
//     * @throws IOException
//     * @throws SignatureException
//     */
//    //    @Transactional
//    ChanpayOrder createOrder(CashOrder order) throws IOException, SignatureException;

    /**
     * 检查是否可以提现一个支付订单
     *
     * @param order 订单
     * @throws IllegalStateException 不可以提现
     */
    void checkWithdrawal(UserOrder order) throws IllegalStateException;

    /**
     * 建立提现订单
     *
     * @param order 对应主订单
     * @return 已建立的提现订单, 如果因为银行之类的外部原因无法创建订单,则会返回null
     * @throws IOException
     * @throws SignatureException
     */
    @ThreadSafe
    ChanpayWithdrawalOrder withdrawalOrder(UserOrder order) throws IOException, SignatureException;

    // 内部方法 请勿调用!!
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    ChanpayWithdrawalOrder withdrawalOrderCore(UserOrder order) throws IOException, SignatureException;

    @PostConstruct
    @Transactional
    void init() throws IOException, SignatureException;

    @EventListener(TradeEvent.class)
    void tradeUpdate(TradeEvent event) throws IOException, SignatureException;

    @EventListener(WithdrawalEvent.class)
    void withdrawalUpdate(WithdrawalEvent event);

    @Transactional
    @ThreadSafe
    void tradeUpdate(MyTradeEvent myEvent) throws IOException, SignatureException;

    @Transactional
    @ThreadSafe
    void withdrawalUpdate(MyWithdrawalEvent myEvent);
}
