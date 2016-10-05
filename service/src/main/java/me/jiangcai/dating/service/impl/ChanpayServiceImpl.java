package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.CashOrder;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class ChanpayServiceImpl extends AbstractChanpayService {

    @Override
    protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
        request.scanPay();
    }
}
