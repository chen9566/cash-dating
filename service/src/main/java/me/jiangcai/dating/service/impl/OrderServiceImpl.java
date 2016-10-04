package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.OrderRepository;
import me.jiangcai.dating.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author CJ
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

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
}
