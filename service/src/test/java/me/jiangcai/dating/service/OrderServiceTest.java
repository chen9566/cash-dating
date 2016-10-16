package me.jiangcai.dating.service;

import me.jiangcai.chanpay.event.AbstractTradeEvent;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class OrderServiceTest extends ServiceBaseTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ChanpayService chanpayService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Test
    public void orderFlows() throws Exception {
        WeixinUserDetail detail = createNewUser();

        User user = userService.byOpenId(detail.getOpenId());
        CashOrder success1Order = orderService.newOrder(user, new BigDecimal("100"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
        changeTime(success1Order, LocalDateTime.now().plusHours(-5));

        CashOrder workingOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
        changeTime(workingOrder, LocalDateTime.now().plusHours(-4));

        CashOrder noCardOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , null);
        changeTime(noCardOrder, LocalDateTime.now().plusHours(-3));

        CashOrder withdrawalFailedOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
        changeTime(withdrawalFailedOrder, LocalDateTime.now().plusHours(-2));


        List<OrderFlow> list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .isEmpty();

        tradeSuccess(success1Order);

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(1);
        assertThat(list.get(0).getOrder())
                .isEqualTo(success1Order);

        tradeSuccess(workingOrder);

        list = orderService.orderFlows(user.getOpenId());

        assertThat(list)
                .hasSize(2);
        assertThat(list.get(0).getOrder())
                .isEqualTo(workingOrder);
        assertThat(list.get(1).getOrder())
                .isEqualTo(success1Order);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);
        assertThat(list.get(1).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);


        // 让它成功
        withdrawalSuccess(orderService.getOne(success1Order.getId()));

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list.get(1).getStatus())
                .isEqualTo(OrderFlowStatus.success);


        tradeSuccess(noCardOrder);

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(3);

        assertThat(list.get(0).getOrder())
                .isEqualTo(noCardOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.cardRequired);

        tradeSuccess(withdrawalFailedOrder);
        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(4);

        assertThat(list.get(0).getOrder())
                .isEqualTo(withdrawalFailedOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);
        withdrawalFailed(orderService.getOne(withdrawalFailedOrder.getId()), WithdrawalStatus.WITHDRAWAL_FAIL, "心情不好吧");

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(4);

        assertThat(list.get(0).getOrder())
                .isEqualTo(withdrawalFailedOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.failed);
    }

    /**
     * 改变订单时间
     *
     * @param order
     * @param time
     */
    private void changeTime(CashOrder order, LocalDateTime time) {
        order.setStartTime(time);
        cashOrderRepository.save(order);
    }

    /**
     * 让这个订单完成支付
     *
     * @param order
     */
    private void tradeSuccess(CashOrder order) throws IOException, SignatureException {
        PlatformOrder platformOrder = orderService.preparePay(order.getId(), PayChannel.weixin);

        TradeEvent tradeEvent = new TradeEvent(TradeStatus.TRADE_SUCCESS);
        mockEventInfo(tradeEvent);
        tradeEvent.setAmount(order.getAmount());
        tradeEvent.setSerialNumber(platformOrder.getId());

        chanpayService.tradeUpdate(tradeEvent);
        System.out.println("1");
    }

    private void mockEventInfo(AbstractTradeEvent tradeEvent) {
        tradeEvent.setTradeTime(LocalDateTime.now());
        tradeEvent.setPlatformOrderNo(UUID.randomUUID().toString().replaceAll("-", ""));
    }


    /**
     * 使这个订单提现失败
     *
     * @param order  订单
     * @param status 转移状态
     * @param reason 原因
     */
    private void withdrawalFailed(CashOrder order, WithdrawalStatus status, String reason) {
        PlatformWithdrawalOrder withdrawalOrder = order.getPlatformWithdrawalOrderSet().stream()
                .max((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .orElseThrow(IllegalStateException::new);

        WithdrawalEvent withdrawalEvent = new WithdrawalEvent(status);
        mockEventInfo(withdrawalEvent);
        withdrawalEvent.setAmount(order.getWithdrawalAmount());
        withdrawalEvent.setSerialNumber(withdrawalOrder.getId());
        if (reason != null)
            withdrawalEvent.setMessage(reason);

        chanpayService.withdrawalUpdate(withdrawalEvent);
        System.out.println("1");
    }

    /**
     * 让这个订单完成提现
     *
     * @param order
     */
    private void withdrawalSuccess(CashOrder order) throws IOException, SignatureException {
        withdrawalFailed(order, WithdrawalStatus.WITHDRAWAL_SUCCESS, null);
    }

}