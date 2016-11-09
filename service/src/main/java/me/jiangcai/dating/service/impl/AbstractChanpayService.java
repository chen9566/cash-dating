package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.data.trade.GetPayChannel;
import me.jiangcai.chanpay.data.trade.PaymentToCard;
import me.jiangcai.chanpay.data.trade.support.EncryptString;
import me.jiangcai.chanpay.data.trade.support.PayChannel;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.chanpay.model.CardAttribute;
import me.jiangcai.chanpay.model.PayMode;
import me.jiangcai.chanpay.model.SubBranch;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.GetPayChannelHandler;
import me.jiangcai.chanpay.service.impl.InstantTradeHandler;
import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.event.MyTradeEvent;
import me.jiangcai.dating.event.MyWithdrawalEvent;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.ChanpayOrderRepository;
import me.jiangcai.dating.repository.ChanpayWithdrawalOrderRepository;
import me.jiangcai.dating.repository.UserOrderRepository;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.ChanpayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 可以通过环境变量
 * cash.bank.auto.forbid 禁止1 允许0  默认允许0
 *
 * @author CJ
 */
public abstract class AbstractChanpayService implements ChanpayService {

    private static final Log log = LogFactory.getLog(AbstractChanpayService.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ChanpayOrderRepository chanpayOrderRepository;
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private BankService bankService;
    @Autowired
    private Environment environment;
    @Autowired
    private ChanpayWithdrawalOrderRepository chanpayWithdrawalOrderRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private CardService cardService;

    @PostConstruct
    @Transactional
    @Override
    public void init() throws IOException, SignatureException {

        // 获取银行列表
        if (environment.getProperty("cash.bank.auto.forbid", Integer.class, 0) == 0) {
            GetPayChannel getPayChannel = new GetPayChannel();
            List<PayChannel> channels = transactionService.execute(getPayChannel, new GetPayChannelHandler());


            final Consumer<PayChannel> payChannelConsumer = payChannel -> {
                log.debug("get bank info " + payChannel);
                bankService.updateBank(payChannel.getCode(), payChannel.getName(), null, null);
            };
            channels = channels.stream()
                    .filter(payChannel -> payChannel.getMode() == PayMode.ONLINE_BANK)
                    .filter(payChannel -> payChannel.getAttribute() == CardAttribute.B)
                    .collect(Collectors.toList());

            if (!environment.acceptsProfiles("test") || channels.size() > bankService.list().size()) {
                // 在测试阶段只有量不够时才干这事儿
                channels.forEach(payChannelConsumer);
            }
        }

    }

    @Override
    @ThreadSafe
    public void withdrawalUpdate(MyWithdrawalEvent myEvent) {
        WithdrawalEvent event = myEvent.getEvent();

        ChanpayWithdrawalOrder order = chanpayWithdrawalOrderRepository.findOne(event.getSerialNumber());
        if (order != null) {
            boolean preFinish = order.isFinish();
            if (preFinish)
                return;
//            boolean preSuccess = order.isSuccess();
            order.setStatus(event.getStatus());
            order.setComment(event.getMessage());

            if (order.isFinish()) {
                order.setFinishTime(LocalDateTime.now());
                if (order.isSuccess()) {
                    //用户订单需更新了
                    order.getUserOrder().withdrawalSuccess();
                } else
                    order.getUserOrder().withdrawalFailed();

                userOrderRepository.save(order.getUserOrder());
            }
            chanpayWithdrawalOrderRepository.save(order);
        } else {
            log.warn("we received tradeEvent " + event + " no in our system.");
        }
    }

    @Override
    public void withdrawalUpdate(WithdrawalEvent event) {
        log.debug("withdrawal event:" + event);
        applicationContext.getBean(ChanpayService.class).withdrawalUpdate(new MyWithdrawalEvent(event));
    }

    @Override
    @ThreadSafe
    public void tradeUpdate(MyTradeEvent myEvent) throws IOException, SignatureException {
        TradeEvent event = myEvent.getEvent();

        if (event.getTradeStatus() != TradeStatus.TRADE_SUCCESS)
            return;

        ChanpayOrder order = chanpayOrderRepository.findOne(event.getSerialNumber());
        if (order != null) {
            boolean preStatus = order.isFinish();
            if (preStatus) {

                return;
            }
            // 校验金额
            order.setStatus(event.getTradeStatus());
            if (order.isFinish()) {
                // order.getCashOrder().getAmount().doubleValue()!=event.getAmount().doubleValue()
                // !order.getCashOrder().getAmount().equals(BigDecimal.valueOf(event.getAmount().doubleValue()))
                if (order.getCashOrder().getAmount().doubleValue() != event.getAmount().doubleValue()) {

                    throw new IllegalStateException("bad amount System:" + order.getCashOrder().getAmount() + " event:" + event.getAmount());
                }
                order.setFinishTime(LocalDateTime.now());
                order.getCashOrder().paySuccess();
                cashOrderRepository.save(order.getCashOrder());
                // 此时应该开启 套现
                applicationContext.getBean(ChanpayService.class).withdrawalOrder(order.getCashOrder());
            }
        } else
            log.warn("we received tradeEvent " + event + " no in our system.");

    }

    @Override
    public void tradeUpdate(TradeEvent event) throws IOException, SignatureException {
        log.debug("trade event:" + event);
        applicationContext.getBean(ChanpayService.class).tradeUpdate(new MyTradeEvent(event));
    }

    @Override
    public ChanpayOrder createOrder(CashOrder order) throws IOException, SignatureException {
//        Card card = order.getOwner().getCards().get(0);
        CreateInstantTrade request = new CreateInstantTrade();
        request.setAmount(order.getAmount());
//        request.setPayerName(card.getOwner());
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

    @Override
    public ChanpayWithdrawalOrder withdrawalOrderCore(UserOrder order) throws IOException, SignatureException {
        log.debug("prepare to withdrawal " + order);
        order = userOrderRepository.getOne(order.getId());
        if (order.isWithdrawalCompleted())
            throw new IllegalStateException("提现已完成。");
        if (order.getPlatformWithdrawalOrderSet() != null && !order.getPlatformWithdrawalOrderSet().isEmpty()) {
            // 检查
            for (PlatformWithdrawalOrder withdrawalOrder : order.getPlatformWithdrawalOrderSet()) {
                if (withdrawalOrder.isSuccess()) {
                    order.withdrawalSuccess();
                    throw new IllegalStateException("提现已完成。");
                }
                if (!withdrawalOrder.isFinish())
                    throw new IllegalStateException("提现正在进行中,请等待。");
            }
        }

        Card card = cardService.recommend(order);

        if (card == null) {
            // 不再是一个例外 而是一个正常逻辑 所以这里无需抛出错误
            log.debug("no card for withdrawal!");
            return null;
        }
//        if (order.getOwner().getCards() == null || order.getOwner().getCards().isEmpty())
//            throw new IllegalStateException("用户尚未绑定银行卡。");

        if (order.getPlatformWithdrawalOrderSet() == null) {
            order.setPlatformWithdrawalOrderSet(new HashSet<>());
        }

        log.debug("ready to withdrawal " + order + " to:" + card);
        ChanpayWithdrawalOrder withdrawalOrder = new ChanpayWithdrawalOrder();
        withdrawalOrder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        withdrawalOrder.setStartTime(LocalDateTime.now());
        withdrawalOrder.setUserOrder(order);

        beforeExecuteWithdrawal(order, withdrawalOrder, card);

        order.getPlatformWithdrawalOrderSet().add(withdrawalOrder);

        order = userOrderRepository.save(order);
        // 先保存,失败自然回滚

        PaymentToCard paymentToCard = new PaymentToCard();
        paymentToCard.setSerialNumber(withdrawalOrder.getId());
        paymentToCard.setAmount(order.getWithdrawalAmount());
        paymentToCard.setProvince(withdrawalOrder.getAddress().getProvince());
        paymentToCard.setCity(withdrawalOrder.getAddress().getCity());
        paymentToCard.setBank(toBank(withdrawalOrder.getBank()));
        SubBranch branch = new SubBranch();
        branch.setName(withdrawalOrder.getSubBranch());
        paymentToCard.setSubBranch(branch);

        paymentToCard.setCardAttribute(CardAttribute.C);
        paymentToCard.setCardName(new EncryptString(withdrawalOrder.getOwner()));
        paymentToCard.setCardNumber(new EncryptString(withdrawalOrder.getNumber()));

        try {
            if (transactionService.execute(paymentToCard, null)) {
                withdrawalOrder.setStatus(WithdrawalStatus.WITHDRAWAL_SUBMITTED);
            } else {
                order.getPlatformWithdrawalOrderSet().remove(withdrawalOrder);
//                userOrderRepository.save(order);
                return null;
            }
        } catch (Exception ex) {
            log.debug("create withdrawal failed", ex);
            order.getPlatformWithdrawalOrderSet().remove(withdrawalOrder);
//            userOrderRepository.save(order);
            return null;
        }

        return withdrawalOrder;
    }

    @Override
    @ThreadSafe
    public ChanpayWithdrawalOrder withdrawalOrder(UserOrder order) throws IOException, SignatureException {
        // 怎么确保安全?
        // 锁定订单号
        return applicationContext.getBean(ChanpayService.class).withdrawalOrderCore(order);
    }

    private me.jiangcai.chanpay.model.Bank toBank(Bank bank) {
        me.jiangcai.chanpay.model.Bank bankModel = new me.jiangcai.chanpay.model.Bank();
        bankModel.setId(bank.getCode());
        bankModel.setName(bank.getName());
        return bankModel;
    }

    protected abstract void beforeExecute(CashOrder order, CreateInstantTrade request);

    protected void beforeExecuteWithdrawal(UserOrder order, ChanpayWithdrawalOrder withdrawalOrder, Card card) {
        withdrawalOrder.setAddress(card.getAddress());
        withdrawalOrder.setBank(card.getBank());
        withdrawalOrder.setSubBranch(card.getSubBranch());
        withdrawalOrder.setOwner(card.getOwner());
        withdrawalOrder.setNumber(card.getNumber());
    }
}
