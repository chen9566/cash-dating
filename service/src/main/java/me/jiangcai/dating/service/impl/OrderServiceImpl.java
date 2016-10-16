package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.dating.repository.CardRepository;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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

    @Override
    public CashOrder newOrder(User user, BigDecimal amount, String comment, Long cardId) {
        if (user == null)
            throw new IllegalArgumentException("owner must not null");
        if (amount.doubleValue() <= 0) {
            throw new IllegalArgumentException("金额不可以是负数。");
        }
        CashOrder order = new CashOrder();
        order.setId(UUID.randomUUID().toString().replace("-", ""));
        order.setOwner(user);
        order.setAmount(amount);
        order.setComment(comment);
        order.setStartTime(LocalDateTime.now());
        order.setThatRateConfig(systemService.currentRateConfig(user));
        if (cardId != null) {
            order.setCard(cardRepository.getOne(cardId));
        }

        return cashOrderRepository.save(order);
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
        if (order.getPlatformOrderSet().stream()
                .filter(PlatformOrder::isFinish)
                .findAny()
                .isPresent()) {
            order.setCompleted(true);
            return true;
        } else
            return false;
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
}
