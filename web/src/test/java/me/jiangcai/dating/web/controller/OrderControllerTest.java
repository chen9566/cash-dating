package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.OrderPage;
import me.jiangcai.dating.service.OrderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author CJ
 */
public class OrderControllerTest extends LoginWebTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void index() throws IOException, SignatureException {
        User user = currentUser();
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        myPage.clickMenu("我的订单");
        OrderPage orderPage = initPage(OrderPage.class);


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

        tradeSuccess(success1Order);
        orderPage.refresh();

    }

}