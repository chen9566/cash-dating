package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.data.trade.GetPayChannel;
import me.jiangcai.chanpay.data.trade.support.PayChannel;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.model.CardAttribute;
import me.jiangcai.chanpay.model.PayMode;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.GetPayChannelHandler;
import me.jiangcai.chanpay.service.impl.InstantTradeHandler;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.repository.ChanpayOrderRepository;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.ChanpayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractChanpayService implements ChanpayService {

    private static final Log log = LogFactory.getLog(AbstractChanpayService.class);
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ChanpayOrderRepository chanpayOrderRepository;
    @Autowired
    private BankService bankService;

    @PostConstruct
    @Transactional
    public void init() throws IOException, SignatureException {
        // 获取银行列表
        //GetPayChannel
        GetPayChannel getPayChannel = new GetPayChannel();
        List<PayChannel> channels = transactionService.execute(getPayChannel, new GetPayChannelHandler());

        channels.stream()
                .filter(payChannel -> payChannel.getMode() == PayMode.ONLINE_BANK)
                .filter(payChannel -> payChannel.getAttribute() == CardAttribute.B)
                .forEach(payChannel -> {
                    log.debug("get bank info " + payChannel);
                    bankService.updateBank(payChannel.getCode(), payChannel.getName());
                });
    }

    @Override
    public void tradeUpdate(TradeEvent event) {
        ChanpayOrder order = chanpayOrderRepository.findOne(event.getSerialNumber());
        if (order != null) {
            boolean preStatus = order.isFinish();
            // 校验金额
            order.setStatus(event.getTradeStatus());
            if (order.isFinish()) {
                if (!order.getCashOrder().getAmount().equals(BigDecimal.valueOf(event.getAmount().doubleValue()))) {
                    throw new IllegalStateException("bad amount System:" + order.getCashOrder().getAmount() + " event:" + event.getAmount());
                }
                if (!preStatus) {
                    order.setFinishTime(LocalDateTime.now());
                }
            }
        } else
            log.warn("we received tradeEvent " + event + " no in our system.");
    }

    @Override
    public ChanpayOrder createOrder(CashOrder order) throws IOException, SignatureException {
        Card card = order.getOwner().getCards().get(0);
        CreateInstantTrade request = new CreateInstantTrade();
        request.setAmount(order.getAmount());
        request.setPayerName(card.getOwner());
        request.setProductName(order.getComment());

        beforeExecute(order, request);

        String url = transactionService.execute(request, new InstantTradeHandler());
        ChanpayOrder chanpayOrder = new ChanpayOrder();
        chanpayOrder.setCashOrder(order);
//        chanpayOrder.setStatus();
        chanpayOrder.setId(request.getSerialNumber());
        chanpayOrder.setUrl(url);
        return chanpayOrder;
    }

    protected abstract void beforeExecute(CashOrder order, CreateInstantTrade request);
}
