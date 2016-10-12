package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author CJ
 */
@Service
@DependsOn("initService")
public class ChanpayServiceImpl extends AbstractChanpayService {

    @Override
    protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
        request.scanPay();
    }

    @Override
    protected void beforeExecute(CashOrder order, ChanpayWithdrawalOrder withdrawalOrder, Card card) {
        // 什么都不用干
        withdrawalOrder.setAddress(card.getAddress());
        withdrawalOrder.setBank(card.getBank());
        withdrawalOrder.setSubBranch(card.getSubBranch());
        withdrawalOrder.setOwner(card.getOwner());
        withdrawalOrder.setNumber(card.getNumber());
    }

    @Override
    public String QRCodeImageFromOrder(ChanpayOrder order) throws IllegalStateException, IOException {
        return order.getUrl();
    }
}
