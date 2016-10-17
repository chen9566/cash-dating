package me.jiangcai.dating.service;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
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
public interface ChanpayService {

    /**
     * 建立支付订单
     *
     * @param order 对应订单
     * @return 已建立的支付订单
     * @throws IOException
     * @throws SignatureException
     */
    //    @Transactional
    ChanpayOrder createOrder(CashOrder order) throws IOException, SignatureException;

    /**
     * 建立提现订单
     *
     * @param order 对应主订单
     * @return 已建立的提现订单, 在资料不充足的情况下可能会返回null
     * @throws IOException
     * @throws SignatureException
     */
    @ThreadSafe
    ChanpayWithdrawalOrder withdrawalOrder(CashOrder order) throws IOException, SignatureException;

    // 内部方法 请勿调用!!
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    ChanpayWithdrawalOrder withdrawalOrderCore(CashOrder order) throws IOException, SignatureException;

    @PostConstruct
    @Transactional
    void init() throws IOException, SignatureException;

    /**
     * 从一个畅捷支付的订单中获取可以获取支付二维码的链接
     *
     * @param order 畅捷订单
     * @return 二维码
     * @throws IllegalStateException 如果这个订单已经无效或者根本没生成
     * @throws IOException           获取的过程发生问题
     */
    String QRCodeImageFromOrder(ChanpayOrder order) throws IllegalStateException, IOException;


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
