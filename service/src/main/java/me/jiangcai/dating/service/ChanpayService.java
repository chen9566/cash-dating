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

    @Transactional
    @EventListener(me.jiangcai.chanpay.event.TradeEvent.class)
    void tradeUpdate(TradeEvent event);
}
