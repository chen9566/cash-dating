package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.InstantTradeHandler;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.service.ChanpayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
public abstract class AbstractChanpayService implements ChanpayService {

    @Autowired
    private TransactionService transactionService;

    @Override
    public ChanpayOrder createOrder(Order order) throws IOException, SignatureException {
        Card card = order.getOwner().getCards().get(0);
        CreateInstantTrade request = new CreateInstantTrade();
        request.setAmount(order.getAmount());
        request.setPayerName(card.getOwner());
        request.setProductName(order.getComment());

        beforeExecute(order, request);

        String url = transactionService.execute(request, new InstantTradeHandler());
        ChanpayOrder chanpayOrder = new ChanpayOrder();
        chanpayOrder.setOrder(order);
//        chanpayOrder.setStatus();
        chanpayOrder.setId(request.getSerialNumber());
        chanpayOrder.setUrl(url);
        return chanpayOrder;
    }

    protected abstract void beforeExecute(Order order, CreateInstantTrade request);
}
