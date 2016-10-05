package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.repository.OrderRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * @author CJ
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ChanpayService chanpayService;

    @Override
    public Order newOrder(User user, BigDecimal amount, String comment) {
        if (user == null)
            throw new IllegalArgumentException("owner must not null");
        if (amount.doubleValue() <= 0) {
            throw new IllegalArgumentException("金额不可以是负数。");
        }
        Order order = new Order();
        order.setId(UUID.randomUUID().toString().replace("-", ""));
        order.setOwner(user);
        order.setAmount(amount);
        order.setComment(comment);
        order.setStartTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order getOne(String id) {
        return orderRepository.getOne(id);
    }

    @Override
    public boolean isComplete(String id) {
        Order order = getOne(id);
        if (order.getPlatformOrderSet() == null || order.getPlatformOrderSet().isEmpty())
            return false;
        //
        return order.getPlatformOrderSet().stream()
                .filter(PlatformOrder::isFinish)
                .findAny()
                .isPresent();
    }

    @Override
    public PlatformOrder preparePay(String id, PayChannel channel) throws IOException, SignatureException {
        // 就一个支付平台啦
        Order order = getOne(id);
        if (order.getPlatformOrderSet() == null) {
            order.setPlatformOrderSet(new HashSet<>());
        }

        if (!order.getPlatformOrderSet().isEmpty())
            return order.getPlatformOrderSet().iterator().next();
        ChanpayOrder chanpayOrder = chanpayService.createOrder(order);
        order.getPlatformOrderSet().add(chanpayOrder);
        orderRepository.save(order);
        return chanpayOrder;
    }

    @Override
    public List<Order> findOrders(String openId) {
        return orderRepository.findByOwnerOrderByStartTimeDesc(userService.byOpenId(openId));
    }
}
