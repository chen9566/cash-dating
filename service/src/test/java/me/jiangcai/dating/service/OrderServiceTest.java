package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class OrderServiceTest extends ServiceBaseTest {

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
//        assertThat(list.get(0).getStatus())
//                .isEqualTo(OrderFlowStatus.cardRequired);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);

        tradeSuccess(withdrawalFailedOrder);
        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(4);

        assertThat(list.get(0).getOrder())
                .isEqualTo(withdrawalFailedOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);
        withdrawalResult(orderService.getOne(withdrawalFailedOrder.getId()), false, "心情不好吧");

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(4);

        assertThat(list.get(0).getOrder())
                .isEqualTo(withdrawalFailedOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.failed);

        // 如果点击重试  那就应该再度进入交易中
        chanpayService.withdrawalOrder(withdrawalFailedOrder);

        list = orderService.orderFlows(user.getOpenId());
        assertThat(list)
                .hasSize(4);

        assertThat(list.get(0).getOrder())
                .isEqualTo(withdrawalFailedOrder);
        assertThat(list.get(0).getStatus())
                .isEqualTo(OrderFlowStatus.transferring);

        //分组
        Object map = orderService.orderFlowsMonthly(user.getOpenId());
        System.out.println(map);
    }


}