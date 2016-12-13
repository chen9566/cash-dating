package me.jiangcai.dating.channel.impl;

import me.jiangcai.chrone.ChroneGateway;
import me.jiangcai.chrone.event.PayStatusChangeEvent;
import me.jiangcai.chrone.exception.ServiceException;
import me.jiangcai.chrone.model.ArbitrageStatus;
import me.jiangcai.chrone.model.OrderInfo;
import me.jiangcai.chrone.model.PayResult;
import me.jiangcai.chrone.model.PaySource;
import me.jiangcai.chrone.model.PayStatus;
import me.jiangcai.chrone.model.TransactionType;
import me.jiangcai.dating.channel.ArbitrageAccountStatus;
import me.jiangcai.dating.channel.ChroneService;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.channel.ChroneOrder;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.exception.ArbitrageBindFailedException;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.PlatformOrderRepository;
import me.jiangcai.dating.service.CardService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Service
public class ChroneServiceImpl implements ChroneService {

    private static final Log log = LogFactory.getLog(ChroneServiceImpl.class);

    @Autowired
    private ChroneGateway chroneGateway;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PlatformOrderRepository platformOrderRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private Environment environment;
    @Autowired
    private CardService cardService;

    @Override
    public void change(PayStatusChangeEvent event) {
        ChroneOrder order = (ChroneOrder) platformOrderRepository.getOne(event.getSerialNumber());
        if (order.isFinish()) {
            log.debug("got change event for finished order:" + event);
            return;
        }
        // 比较金额
        if (order.getCashOrder().getAmount().doubleValue() != event.getAmount().doubleValue()) {
            throw new IllegalStateException("bad amount System:" + order.getCashOrder().getAmount() + " event:" + event.getAmount());
        }

        order.setStatus(event.getPayStatus());
        if (order.isFinish()) {
            order.setFinishTime(LocalDateTime.now());
            if (order.getStatus() == PayStatus.success) {
                order.getCashOrder().paySuccess();
                applicationEventPublisher.publishEvent(new Notification(order.getCashOrder().getOwner()
                        , NotifyType.orderPaid
                        , "/orderDetail/" + order.getCashOrder().getId()
                        , order.getCashOrder()
                        , order.getCashOrder().getFriendlyId()
                        , order.getCashOrder().getComment()
                        , order.getCashOrder().getAmount()
                        , order.getFinishTime()));
                cashOrderRepository.save(order.getCashOrder());
            }

        }

    }


    @Override
    public int lowestAmount() {
        return 10;
    }

    @Override
    public boolean arbitrageResultManually() {
        return true;
    }

    @Override
    public void checkArbitrageResult(PlatformOrder order) throws IOException, SignatureException {
        try {
            order = platformOrderRepository.getOne(order.getId());
            final CashOrder cashOrder = order.getCashOrder();
            if (cashOrder.isWithdrawalCompleted()) {
                log.debug("do not check a WithdrawalCompleted order.");
                return;
            }
            ArbitrageStatus status = chroneGateway.queryArbitrage(order.getId());
            switch (status) {
                case success:
                    arbitrageSuccess(cashOrder);
                    break;
                case failed:
                    arbitrageFailed(cashOrder, "套现结果:失败");
                    break;
//                case closed:
//                    arbitrageFailed(cashOrder, "套现结果:关闭");
//                    break;
                default:
                    // do nothing;
            }
        } catch (ServiceException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public boolean checkPayResult(PlatformOrder order) throws IOException, SignatureException {
        try {
            PayResult result = chroneGateway.queryOrder(order.getId());
            switch (result.getStatus()) {
                case success:
                    applicationEventPublisher.publishEvent(result.toEvent());
                    return true;
                default:
                    // do nothing;
            }
        } catch (ServiceException ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        return false;
    }

    private void arbitrageFailed(CashOrder cashOrder, String comment) {
        cashOrder.setSystemComment(comment);
        cashOrder.withdrawalTransferFailedNotification(null, comment);
    }

    private void arbitrageSuccess(CashOrder cashOrder) {
        cashOrder.withdrawalSuccess();
        cashOrder.setSystemComment("套现结果:成功");
    }

    @Override
    public boolean useOneOrderForPayAndArbitrage() {
        return true;
    }

    @Override
    public void bindUser(User user) throws IOException, SignatureException {
        Card card = cardService.recommend(user);
        if (card.getOwnerId() == null)
            throw new IOException("必须填写身份证号码");
        try {
            chroneGateway.register(card.getSubBranchBank().getCode(), card.getOwnerId(), user.getMobileNumber(), "XZA123"
                    , card.getNumber(), card.getOwner(), null, null, null, null, null);
        } catch (ServiceException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public ArbitrageAccountStatus bindingUserStatus(User user) throws IOException, SignatureException
            , ArbitrageBindFailedException {
        try {
            switch (chroneGateway.accountStatus(user.getMobileNumber())) {
                case unknown:
                    return ArbitrageAccountStatus.notYet;
                case success:
                    return ArbitrageAccountStatus.done;
                default:
                    return ArbitrageAccountStatus.auditing;
            }
        } catch (ServiceException e) {
            if (e.getCode().equals("411"))
                return ArbitrageAccountStatus.notYet;
            throw new IOException(e.getMessage(), e);
        }
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
            chroneOrder.setStatus(PayStatus.wait);
            chroneOrder.setUrl(orderInfo.getScanImageURL());
            chroneOrder.setId(orderInfo.getId());
            chroneOrder.setCashOrder(order);
            return platformOrderRepository.save(chroneOrder);
        } catch (ServiceException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void mockArbitrageResult(CashOrder order, boolean success, String reason) {
        order = cashOrderRepository.getOne(order.getId());
        if (success) {
            arbitrageSuccess(order);
        } else
            arbitrageFailed(order, reason);
    }

    @Override
    public String QRCodeImageFromOrder(PlatformOrder order) throws IllegalStateException, IOException {
        return environment.getProperty("dating.url", "http://localhost") + "/toQR?text="
                + URLEncoder.encode(order.getUrl(), "UTF-8");
    }
}
