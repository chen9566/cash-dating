package me.jiangcai.dating.mock;

import me.jiangcai.chanpay.data.Request;
import me.jiangcai.chanpay.data.Response;
import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.data.trade.GetPayChannel;
import me.jiangcai.chanpay.data.trade.PaymentToCard;
import me.jiangcai.chanpay.data.trade.TradeRequest;
import me.jiangcai.chanpay.exception.ServiceException;
import me.jiangcai.chanpay.exception.SystemException;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.PayHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collections;
import java.util.UUID;

/**
 * @author CJ
 */
@SuppressWarnings("unchecked")
@Primary
@Service
public class MockTransactionService implements TransactionService {

    /**
     * 设定该卡number可让提现直接订单失败
     */
    public static String FailedServiceCardNumber = null;

    @Override
    public Response execute(Request request) throws IOException, SignatureException, SystemException, ServiceException {
        return null;
    }

    @Override
    public <T> T execute(TradeRequest tradeRequest, PayHandler<T> payHandler) throws IOException, SignatureException {
        if (tradeRequest instanceof GetPayChannel) {
            // List<PayChannel>
            return (T) Collections.emptyList();
        }
        if (tradeRequest instanceof CreateInstantTrade) {
            CreateInstantTrade trade = (CreateInstantTrade) tradeRequest;
            trade.setSerialNumber(UUID.randomUUID().toString().replaceAll("-", ""));
            return (T) ("trade://" + trade.getSerialNumber());
        }
        if (tradeRequest instanceof PaymentToCard) {
            if (FailedServiceCardNumber != null && ((PaymentToCard) tradeRequest).getCardNumber().getOrigin().equals(FailedServiceCardNumber)) {
                throw new ServiceException("01", "就是失败了");
            }
            return (T) Boolean.TRUE;
        }
        return null;
    }
}
