package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.OrderFlows;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.dating.repository.CardRepository;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.PayToUserOrderRepository;
import me.jiangcai.dating.repository.UserOrderRepository;
import me.jiangcai.dating.repository.WithdrawOrderRepository;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.lib.seext.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Log log = LogFactory.getLog(OrderServiceImpl.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月", Locale.CHINA);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ChanpayService chanpayService;
    @Autowired
    private SystemService systemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CardRepository cardRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PayToUserOrderRepository payToUserOrderRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private StatisticService statisticService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public CashOrder newOrder(User user, BigDecimal amount, String comment, Long cardId) {
        CashOrder order = new CashOrder();
        forNewCashOrder(user, amount, comment, cardId, order);

        return cashOrderRepository.save(order);
    }

    private void forNewCashOrder(User user, BigDecimal amount, String comment, Long cardId, CashOrder order) {
        forNewUserOrder(user, amount, comment, cardId, order);
        order.setThatRateConfig(systemService.currentRateConfig(user));
    }

    private void forNewUserOrder(User user, BigDecimal amount, String comment, Long cardId, UserOrder order) {
        if (user == null)
            throw new IllegalArgumentException("owner must not null");
        if (amount.doubleValue() <= 0) {
            throw new IllegalArgumentException("金额不可以是负数。");
        }

        order.setId(UUID.randomUUID().toString().replace("-", ""));
        order.setOwner(user);
        order.setAmount(amount);
        order.setComment(comment);
        order.setStartTime(LocalDateTime.now());
        if (cardId != null) {
            order.setCard(cardRepository.getOne(cardId));
        }
    }

    @Override
    public CashOrder getOne(String id) {
        return cashOrderRepository.getOne(id);
    }

    @Override
    public boolean isComplete(String id) {
        CashOrder order = getOne(id);

        if (order.isCompleted())
            return true;

        if (order.getPlatformOrderSet() == null || order.getPlatformOrderSet().isEmpty())
            return false;
        //
        //            order.setCompleted(true);
        return order.getPlatformOrderSet().stream()
                .filter(PlatformOrder::isFinish)
                .findAny()
                .isPresent();
    }

    @Override
    public PlatformOrder preparePay(String id, PayChannel channel) throws IOException, SignatureException {
        // 就一个支付平台啦
        CashOrder order = getOne(id);
        if (order.getPlatformOrderSet() == null) {
            order.setPlatformOrderSet(new HashSet<>());
        }

        if (!order.getPlatformOrderSet().isEmpty())
            return order.getPlatformOrderSet().iterator().next();

        PlatformOrder order1 = systemService.arbitrageChannel(channel).newOrder(order, channel);
//        ChanpayOrder chanpayOrder = chanpayService.createOrder(order);
        order.getPlatformOrderSet().add(order1);
        cashOrderRepository.save(order);
        return order1;
    }

    @Override
    public List<CashOrder> findOrders(String openId) {
        return cashOrderRepository.findByOwnerOrderByStartTimeDesc(userService.byOpenId(openId));
    }

    @Override
    public List<OrderFlow> orderFlows(String openId) {
        final List<?> list = cashOrderRepository.findOrderFlow(userService.byOpenId(openId));
        return toOrderFlowList(list);
    }

    private List<OrderFlow> toOrderFlowList(List<?> list) {
        ArrayList<OrderFlow> flowArrayList = new ArrayList<>();
        list.forEach(object -> {
            Object[] objects = (Object[]) object;
            OrderFlow flow = new OrderFlow();
            flow.setOrder((CashOrder) objects[0]);
            final CashOrder cashOrder = flow.getOrder();
            checkArbitrage(cashOrder);
            if (objects.length >= 2) {
                PlatformWithdrawalOrder withdrawalOrder = (PlatformWithdrawalOrder) objects[1];
                if (withdrawalOrder != null) {
                    if (withdrawalOrder.isFinish()) {
                        if (withdrawalOrder.isSuccess()) {
                            flow.setStatus(OrderFlowStatus.success);
                        } else
                            flow.setStatus(OrderFlowStatus.failed);
                    } else {
                        flow.setStatus(OrderFlowStatus.transferring);
                    }
                    flow.setWithdrawalOrder(withdrawalOrder);
                } else {
                    arbitrageNotFound(flow);
                }
            } else {
                arbitrageNotFound(flow);

            }

            if (!flowArrayList.contains(flow))
                flowArrayList.add(flow);
        });
        return flowArrayList;
    }

    @Override
    public void checkArbitrage(CashOrder cashOrder) {
        if (cashOrder.isCompleted() && !cashOrder.isWithdrawalCompleted() && cashOrder.getPlatformOrderSet() != null)
            cashOrder.getPlatformOrderSet().stream()
                    .filter(PlatformOrder::isFinish)
                    .findFirst()
                    .ifPresent(platformOrder -> {
                        ArbitrageChannel channel = applicationContext.getBean(platformOrder.channelClass());
                        if (channel.arbitrageResultManually()) {
                            try {
                                channel.checkArbitrageResult(platformOrder);
                            } catch (Exception ex) {
                                log.debug("checkArbitrageResult", ex);
                            }
                        }
                    });
    }

    private void arbitrageNotFound(OrderFlow flow) {
        flow.setStatus(OrderFlowStatus.cardRequired);
        // 也不见得
//                获取支付订单
        PlatformOrder platformOrder = flow.getOrder().getPlatformOrderSet().stream()
                .filter(PlatformOrder::isFinish).findFirst().orElseThrow(IllegalStateException::new);
        ArbitrageChannel channel = applicationContext.getBean(platformOrder.channelClass());
        if (channel.useOneOrderForPayAndArbitrage()) {
            if (flow.getOrder().isWithdrawalCompleted()) {
                flow.setStatus(OrderFlowStatus.success);
            } else if (flow.getOrder().getSystemComment() != null) {
                flow.setStatus(OrderFlowStatus.failed);
            } else
                flow.setStatus(OrderFlowStatus.transferring);
        }
    }

    @Override
    public List<OrderFlow> finishedOrderFlows(String openId) {
        final List<?> list = cashOrderRepository.findFinishedOrderFlow(userService.byOpenId(openId));
        return toOrderFlowList(list);
    }

    @Override
    public List<OrderFlows> orderFlowsMonthly(String openId) {
        final List<OrderFlow> flows = orderFlows(openId);

        return toMonthly(flows);
    }

    private List<OrderFlows> toMonthly(List<OrderFlow> flows) {
        ArrayList<OrderFlows> result = new ArrayList<>();
        flows.stream()
                .map(orderFlow -> LocalDate.of(orderFlow.getOrder().getStartTime().getYear(), orderFlow.getOrder().getStartTime().getMonth(), 1))
                .distinct()
                .forEach(localDate -> {
                    result.add(new OrderFlows(flows.stream()
                            .filter(orderFlow -> orderFlow.getOrder().getStartTime().getYear() == localDate.getYear() && orderFlow.getOrder().getStartTime().getMonth() == localDate.getMonth())
                            .collect(Collectors.toList()), formatter.format(localDate)));
                });
        return result;
    }

    @Override
    public List<OrderFlows> finishedOrderFlowsMonthly(String openId) {
        return toMonthly(finishedOrderFlows(openId));
    }

    // 提现供应商?
    @Override
    public ChanpayWithdrawalOrder withdrawalWithCard(String orderId, Long cardId) throws IOException, SignatureException {
        CashOrder order = getOne(orderId);
        if (!order.isCompleted())
            throw new IllegalStateException("订单尚未完成支付。");
        if (cardId != null) {
            order.setCard(cardRepository.getOne(cardId));
            cashOrderRepository.save(order);
        }
        return chanpayService.withdrawalOrder(order);
    }

    @Override
    public PayToUserOrder newPayToOrder(String openid, HttpServletRequest request, User user, BigDecimal amount
            , String comment) {
        PayToUserOrder order = new PayToUserOrder();
        forNewCashOrder(user, amount, comment, null, order);
        User from = userService.byOpenId(openid);
        if (from == null) {
            from = userService.newUser(openid, request);
        }
        order.setFrom(from);
        return payToUserOrderRepository.save(order);
    }

    @Override
    @ThreadSafe
    public WithdrawOrder newWithdrawOrder(User user, BigDecimal amount, Long cardId) throws IOException, SignatureException {
        //  应该是通过最安全的方式检查余额
        if (statisticService.balance(user.getOpenId()).compareTo(amount) == -1) {
            throw new IllegalStateException("余额不足");
        }
        WithdrawOrder order = new WithdrawOrder();
        forNewUserOrder(user, amount, "提现", cardId, order);
        order = withdrawOrderRepository.save(order);
        // 提现
        ChanpayWithdrawalOrder chanpayWithdrawalOrder = chanpayService.withdrawalOrder(order);
        if (chanpayWithdrawalOrder == null) {
            // 这就很尴尬了!!!
            withdrawOrderRepository.delete(order);
            throw new IllegalStateException("卡尚未绑定,无法提现");
        } else {
            order = withdrawOrderRepository.getOne(order.getId());
            order.setProcessStatus(WithdrawOrderStatus.requested);
            withdrawOrderRepository.save(order);
            return order;
        }
    }

    @Override
    public List<UserOrder> queryUserOrders(String search) {
        try {
            search = NumberUtils.hash62ToUUID(search).toString().replaceAll("-", "");
        } catch (Throwable ignored) {
        }
        return userOrderRepository.findAll(specification(search));
    }

    private Specification<UserOrder> specification(String search) {
        return (root, query, cb) -> {
            query.distinct(true);
            SetJoin<UserOrder, PlatformWithdrawalOrder> platformWithdrawalOrderSet
                    = root.joinSet("platformWithdrawalOrderSet", JoinType.LEFT);
            Root<CashOrder> cashOrderRoot = cb.treat(root, CashOrder.class);
            SetJoin<CashOrder, PlatformOrder> platformOrderSet = cashOrderRoot.joinSet("platformOrderSet", JoinType.LEFT);
//                cb.treat(platformOrderSet,CashOrder.class)
//            cb.in(platformWithdrawalOrderSet.get("id"))
            return cb.or(
                    cb.equal(root.get("id"), search)
                    , cb.equal(root.get("owner").get("nickname"), search)
                    , cb.equal(platformWithdrawalOrderSet.get("id"), search)
                    , cb.equal(platformOrderSet.get("id"), search)
            );
        };
    }
}
