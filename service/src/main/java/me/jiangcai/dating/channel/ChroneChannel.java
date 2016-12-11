package me.jiangcai.dating.channel;

import me.jiangcai.chrone.ChroneGateway;
import me.jiangcai.chrone.exception.ServiceException;
import me.jiangcai.chrone.model.OrderInfo;
import me.jiangcai.chrone.model.PaySource;
import me.jiangcai.chrone.model.PayStatus;
import me.jiangcai.chrone.model.TransactionType;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.channel.ChroneOrder;
import me.jiangcai.dating.repository.PlatformOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
@Component
public class ChroneChannel implements ArbitrageChannel {

    @Autowired
    private ChroneGateway chroneGateway;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PlatformOrderRepository platformOrderRepository;

    @Override
    public boolean useOneOrderForPayAndArbitrage() {
        return true;
    }

    @Override
    public boolean debitCardManageable() {
        return true;
    }

    @Override
    public PlatformOrder newOrder(CashOrder order) throws IOException, SignatureException {
        try {
            OrderInfo orderInfo = chroneGateway.createScanOrder(PaySource.weixin, order.getOwner().getMobileNumber()
                    , order.getAmount()
                    , order.getWithdrawalAmount(), TransactionType.T0, null, null, null);
            ChroneOrder chroneOrder = new ChroneOrder();
            chroneOrder.setPayStatus(PayStatus.wait);
            chroneOrder.setUrl(orderInfo.getScanImageURL());
            chroneOrder.setId(orderInfo.getId());
            chroneOrder.setCashOrder(order);
            return platformOrderRepository.save(chroneOrder);
        } catch (ServiceException e) {
            throw new IOException(e.getMessage());
        }
    }
}
