package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.PayChannel;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.List;

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

    @Transactional(readOnly = true)
    Order getOne(String id);

    /**
     * @param id
     * @return 这个订单是否已完成
     */
    @Transactional(readOnly = true)
    boolean isComplete(String id);

    /**
     * 准备支付,这个过程也就是建立支付平台订单,为了向下兼容我加入了渠道信息
     *
     * @param id      主订单号
     * @param channel 渠道,默认微信
     * @return
     */
    @Transactional
    PlatformOrder preparePay(String id, PayChannel channel) throws IOException, SignatureException;

    /**
     * 应该是按照时间降序
     *
     * @param openId openId
     * @return 这个用户相关的所有订单
     */
    @Transactional(readOnly = true)
    List<Order> findOrders(String openId);

}
