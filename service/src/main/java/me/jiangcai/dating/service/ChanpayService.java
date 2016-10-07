package me.jiangcai.dating.service;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import org.springframework.context.event.EventListener;
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

    //    @Transactional
    ChanpayOrder createOrder(CashOrder order) throws IOException, SignatureException;

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

    @Transactional
    @EventListener(me.jiangcai.chanpay.event.TradeEvent.class)
    void tradeUpdate(TradeEvent event);
}
