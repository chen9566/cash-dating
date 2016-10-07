package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author CJ
 */
@Service
public class ChanpayServiceImpl extends AbstractChanpayService {

    @Override
    protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
        request.scanPay();
    }

    @Override
    public String QRCodeImageFromOrder(ChanpayOrder order) throws IllegalStateException, IOException {
        return order.getUrl();
    }
}
