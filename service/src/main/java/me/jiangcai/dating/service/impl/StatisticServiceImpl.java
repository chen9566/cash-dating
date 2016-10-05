package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.repository.WithdrawOrderRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public BigDecimal totalExpense(String openId) {
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementExpense());

        for (CashOrder order : orderService.findOrders(openId)) {
            // 必须得完成才可以算吧?
            if (orderService.isComplete(order.getId()))
                total = total.add(order.getAmount());
        }

        return total;
    }

    @Override
    public BigDecimal balance(String openId) {
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementBalance());

        for (WithdrawOrder order : withdrawOrderRepository.findByOwnerOrderByStartTimeDesc(user)) {
            if (order.getProcessStatus() == WithdrawOrderStatus.cancelled)
                continue;
            total = total.subtract(order.getAmount());
        }

        return total;
    }
}
