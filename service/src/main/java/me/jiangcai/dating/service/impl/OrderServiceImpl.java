package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.dating.repository.CardRepository;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.PayToUserOrderRepository;
import me.jiangcai.dating.repository.WithdrawOrderRepository;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ChanpayService chanpayService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PayToUserOrderRepository payToUserOrderRepository;
    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private StatisticService statisticService;

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
        ChanpayOrder chanpayOrder = chanpayService.createOrder(order);
        order.getPlatformOrderSet().add(chanpayOrder);
        cashOrderRepository.save(order);
        return chanpayOrder;
    }

    @Override
    public List<CashOrder> findOrders(String openId) {
        return cashOrderRepository.findByOwnerOrderByStartTimeDesc(userService.byOpenId(openId));
    }

    @Override
    public List<OrderFlow> orderFlows(String openId) {
        ArrayList<OrderFlow> flowArrayList = new ArrayList<>();
        cashOrderRepository.findOrderFlow(userService.byOpenId(openId)).forEach(object -> {
            Object[] objects = (Object[]) object;
            OrderFlow flow = new OrderFlow();
            flow.setOrder((CashOrder) objects[0]);
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
                } else
                    flow.setStatus(OrderFlowStatus.cardRequired);
            } else {
                flow.setStatus(OrderFlowStatus.cardRequired);
            }

            if (!flowArrayList.contains(flow))
                flowArrayList.add(flow);
        });
        return flowArrayList;
    }

    @Override
    public Map<LocalDate, List<OrderFlow>> orderFlowsMonthly(String openId) {
        final List<OrderFlow> flows = orderFlows(openId);

        HashMap<LocalDate, List<OrderFlow>> result = new HashMap<>();
        flows.stream()
                .map(orderFlow -> LocalDate.of(orderFlow.getOrder().getStartTime().getYear(), orderFlow.getOrder().getStartTime().getMonth(), 1))
                .distinct()
                .forEach(localDate -> {
                    result.put(localDate, flows.stream()
                            .filter(orderFlow -> orderFlow.getOrder().getStartTime().getYear() == localDate.getYear() && orderFlow.getOrder().getStartTime().getMonth() == localDate.getMonth())
                            .collect(Collectors.toList()));
                });
//        cashOrderRepository.findOrderFlowMonthly(userService.byOpenId(openId));
        return result;
    }

    @Override
    public ChanpayWithdrawalOrder withdrawalWithCard(String orderId, Long cardId) throws IOException, SignatureException {
        CashOrder order = getOne(orderId);
        if (cardId != null) {
            order.setCard(cardRepository.getOne(cardId));
        }
        cashOrderRepository.save(order);
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
}
