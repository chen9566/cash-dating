package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.Order;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class ChanpayServiceImpl extends AbstractChanpayService {

    @Override
    protected void beforeExecute(Order order, CreateInstantTrade request) {
        request.scanPay();
    }
}
