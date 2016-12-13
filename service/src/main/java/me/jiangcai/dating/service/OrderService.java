package me.jiangcai.dating.service;

import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.OrderFlows;
import me.jiangcai.dating.model.PayChannel;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
     * @param cardId  套现的卡,如果卡未设置,则暂时不套现
     * @return 订单
     */
    @Transactional
    CashOrder newOrder(User user, BigDecimal amount, String comment, Long cardId);

    @Transactional(readOnly = true)
    CashOrder getOne(String id);

    /**
     * @param id 主订单号
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
    List<CashOrder> findOrders(String openId);

    /**
     * 近期的订单流水
     *
     * @param openId 用户
     * @return 订单流水
     */
    @Transactional(readOnly = true)
    List<OrderFlow> orderFlows(String openId);

    /**
     * 检查到款状态
     *
     * @param cashOrder 相关订单
     */
    @Transactional
    void checkArbitrage(CashOrder cashOrder);

    /**
     * 已完成的订单流水
     *
     * @param openId 用户
     * @return 订单流水
     */
    @Transactional(readOnly = true)
    List<OrderFlow> finishedOrderFlows(String openId);

    /**
     * 近期的订单流水
     * @param openId 用户
     * @return 按月分组的订单流水
     */
    @Transactional(readOnly = true)
    List<OrderFlows> orderFlowsMonthly(String openId);

    /**
     * 近期的订单流水
     *
     * @param openId 用户
     * @return 按月分组的订单流水
     */
    @Transactional(readOnly = true)
    List<OrderFlows> finishedOrderFlowsMonthly(String openId);

    /**
     * 尝试重新提现
     *
     * @param orderId 主订单号
     * @param cardId  卡号,可选,如果为null就是不改变
     * @return 新增的提现订单
     */
//    @Transactional 这个并不需要开启事务,而是应该立刻提交 这样新的事务才可以获得新的数据
    ChanpayWithdrawalOrder withdrawalWithCard(String orderId, Long cardId) throws IOException, SignatureException;

    /**
     * 创建支付给他人的订单
     *
     * @param openid  付款方
     * @param request 当前http请求
     * @param user    收款方
     * @param amount  金额
     * @param comment 事由
     * @return 新增的订单
     */
    @Transactional
    PayToUserOrder newPayToOrder(String openid, HttpServletRequest request, User user, BigDecimal amount, String comment);

    /**
     * 创建提现订单
     *
     * @param user   用户
     * @param amount 金额
     * @param cardId 可选的卡号
     * @return 新建的订单
     */
    @ThreadSafe
    WithdrawOrder newWithdrawOrder(User user, BigDecimal amount, Long cardId) throws IOException, SignatureException;

    List<UserOrder> queryUserOrders(String search);
}
