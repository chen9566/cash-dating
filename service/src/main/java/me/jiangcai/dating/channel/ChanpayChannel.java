package me.jiangcai.dating.channel;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.service.ChanpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
@Component
public class ChanpayChannel implements ArbitrageChannel {

    @Autowired
    private ChanpayService chanpayService;

    @Override
    public boolean useOneOrderForPayAndArbitrage() {
        return false;
    }

    @Override
    public boolean debitCardManageable() {
        return false;
    }

    @Override
    public PlatformOrder newOrder(CashOrder order) throws IOException, SignatureException {
        return chanpayService.createOrder(order);
    }
}
