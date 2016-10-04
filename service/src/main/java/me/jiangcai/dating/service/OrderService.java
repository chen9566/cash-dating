package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 订单和支付系统
 *
 * @author CJ
 */
public interface OrderService {

    /**
     * 新增一个付款订单
     *
     * @param user    所有者
     * @param amount  金额
     * @param comment 备注
     * @return 订单
     */
    @Transactional
    Order newOrder(User user, BigDecimal amount, String comment);

}
