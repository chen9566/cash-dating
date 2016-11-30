package me.jiangcai.dating.mock;

import me.jiangcai.chanpay.data.Request;
import me.jiangcai.chanpay.data.Response;
import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.data.trade.GetPayChannel;
import me.jiangcai.chanpay.data.trade.PaymentToCard;
import me.jiangcai.chanpay.data.trade.QueryTrade;
import me.jiangcai.chanpay.data.trade.QueryTradeResult;
import me.jiangcai.chanpay.data.trade.TradeRequest;
import me.jiangcai.chanpay.exception.ServiceException;
import me.jiangcai.chanpay.exception.SystemException;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.PayHandler;
import me.jiangcai.dating.repository.ChanpayOrderRepository;
import me.jiangcai.dating.repository.ChanpayWithdrawalOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;
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
    @Autowired
    private ChanpayOrderRepository chanpayOrderRepository;
    @Autowired
    private ChanpayWithdrawalOrderRepository chanpayWithdrawalOrderRepository;

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
        if (tradeRequest instanceof QueryTrade) {
            QueryTrade queryTrade = (QueryTrade) tradeRequest;
            // 是
            switch (queryTrade.getType()) {
                case INSTANT:
                    QueryTradeResult queryTradeResult = new QueryTradeResult();
                    queryTradeResult.setSerialNumber(queryTrade.getSerialNumber());
                    queryTradeResult.setChanPayNumber(UUID.randomUUID().toString().replace("-", ""));
                    queryTradeResult.setStatus(TradeStatus.TRADE_FINISHED);
                    queryTradeResult.setTime(LocalDateTime.now());
                    // 金额?
                    queryTradeResult.setAmount(chanpayOrderRepository.getOne(queryTrade.getSerialNumber()).getCashOrder().getAmount());
                    return (T) queryTradeResult;
                case WITHDRAWAL:
                    queryTradeResult = new QueryTradeResult();
                    queryTradeResult.setSerialNumber(queryTrade.getSerialNumber());
                    queryTradeResult.setChanPayNumber(UUID.randomUUID().toString().replace("-", ""));
                    queryTradeResult.setStatus(TradeStatus.success);
                    queryTradeResult.setTime(LocalDateTime.now());
                    // 金额?
                    queryTradeResult.setAmount(chanpayWithdrawalOrderRepository.getOne(queryTrade.getSerialNumber()).getUserOrder().getWithdrawalAmount());
                    return (T) queryTradeResult;
            }
        }
        return null;
    }
}
