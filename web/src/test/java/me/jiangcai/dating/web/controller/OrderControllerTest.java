package me.jiangcai.dating.web.controller;

import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.OrderPage;
import me.jiangcai.dating.service.OrderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Repeat;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class OrderControllerTest extends LoginWebTest {

    @Autowired
    private OrderService orderService;

    /**
     * 安全测试,即同时提起重试请求
     */
    @Test
    @Repeat(20)
    public void security() throws Exception {
        User user = currentUser();
        CashOrder withdrawalFailedOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
        changeTime(withdrawalFailedOrder, LocalDateTime.now().plusHours(-2));
        tradeSuccess(withdrawalFailedOrder);
        withdrawalFailed(withdrawalFailedOrder, WithdrawalStatus.WITHDRAWAL_FAIL, "怀孕了?");

        MockHttpSession session = mvcLogin();
        //
        int times = orderService.getOne(withdrawalFailedOrder.getId()).getPlatformWithdrawalOrderSet().size();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(postWeixin("/touchOrder").param("id", withdrawalFailedOrder.getId()).session(session))
                            .andExpect(status().isFound());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        int count = 20;
        while (count-- > 0) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        }

        Thread.sleep(2000);

        assertThat(orderService.getOne(withdrawalFailedOrder.getId()).getPlatformWithdrawalOrderSet().size())
                .isEqualTo(times + 1);
    }

    private OrderPage bindCardOnOrderPage(String mobile, OrderPage page, SubBranchBank bank, String owner, String number) {
        page.toCreateNewCard();

        BindingCardPage cardPage = initPage(BindingCardPage.class);
        // 这个用户已经产生
        assertThat(userService.byMobile(mobile))
                .isNotNull();
        //
        // 地址自己选吧


        cardPage.submitWithRandomAddress(bank, owner, number);
        return initPage(OrderPage.class);
    }

    @Test
    public void withoutCard() {
        User user = currentUser();
        cardService.deleteCards(user.getOpenId());

        CashOrder success1Order = orderService.newOrder(user, new BigDecimal("100"), UUID.randomUUID().toString()
                , null);
        changeTime(success1Order, LocalDateTime.now().plusHours(-5));

        CashOrder workingOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , null);
        changeTime(workingOrder, LocalDateTime.now().plusHours(-4));

        CashOrder noCardOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , null);
        changeTime(noCardOrder, LocalDateTime.now().plusHours(-3));

        CashOrder withdrawalFailedOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , null);
        changeTime(withdrawalFailedOrder, LocalDateTime.now().plusHours(-2));

        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        myPage.clickMenu("我的订单");
        OrderPage orderPage = initPage(OrderPage.class);

        orderPage.assertWithoutCards();

        // 点击然后回来 应该还是
        SubBranchBank subBranchBank = randomSubBranchBank();

        String owner = RandomStringUtils.randomAlphanumeric(3);
        String number = randomBankCard();
        orderPage = bindCardOnOrderPage(user.getMobileNumber(), orderPage, subBranchBank, owner, number);

        orderPage.assertCards();
    }

    @Test
    public void index() throws IOException, SignatureException {
        User user = currentUser();
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        myPage.clickMenu("我的订单");
        OrderPage orderPage = initPage(OrderPage.class);

        orderPage.assertCards();

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
        withdrawalSuccess(success1Order);
        orderPage.refresh();

        tradeSuccess(workingOrder);
        tradeSuccess(noCardOrder);
        tradeSuccess(withdrawalFailedOrder);
        withdrawalFailed(withdrawalFailedOrder, WithdrawalStatus.WITHDRAWAL_FAIL, "怀孕了?");
        orderPage.refresh();

        orderPage.choseCard(noCardOrder.getId(), user.getCards().get(0));
        orderPage.reloadPageInfo();
        List<OrderFlow> flowList = orderService.orderFlows(user.getOpenId());

        assertThat(flowList.get(1).getStatus())
                .isEqualByComparingTo(OrderFlowStatus.transferring);

        orderPage.retry(withdrawalFailedOrder.getId());
        orderPage.reloadPageInfo();
        flowList = orderService.orderFlows(user.getOpenId());
        assertThat(flowList.get(0).getStatus())
                .isEqualByComparingTo(OrderFlowStatus.transferring);

    }

}