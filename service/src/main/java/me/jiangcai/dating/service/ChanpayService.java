package me.jiangcai.dating.service;

import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.Order;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SignatureException;

/**
 * 到畅捷支付的服务
 *
 * @author CJ
 */
public interface ChanpayService {

    //    @Transactional
    ChanpayOrder createOrder(Order order) throws IOException, SignatureException;

    @Transactional
    @EventListener(me.jiangcai.chanpay.event.TradeEvent.class)
    void tradeUpdate(TradeEvent event);
}
