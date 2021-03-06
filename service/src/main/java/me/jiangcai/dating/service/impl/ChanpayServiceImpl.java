package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
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
    public String QRCodeImageFromOrder(PlatformOrder order) throws IllegalStateException, IOException {
        return order.getUrl();
    }
}
