package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.page.ManageOrderPage;
import me.jiangcai.dating.page.ManageOrderResultPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.repository.WithdrawOrderRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@AsManage(ManageStatus.waiter)
public class ManageOrderControllerTest extends ManageWebTest {

    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void go() throws IOException, SignatureException {
        // 建立数据
        User user = createClassicsUsers().get("a");
        user = userRepository.getOne(user.getId());
        user.setNickname(UUID.randomUUID().toString().replaceAll("-", "").substring(1, 10));
        user.setSettlementBalance(new BigDecimal(10000000));
        userRepository.save(user);

        final CashOrder cashOrder = orderService.getOne(makeFinishCashOrder(user, randomOrderAmount(), UUID.randomUUID().toString()).getId());
        String word1 = cashOrder.getId();
        String word2 = cashOrder.getFriendlyId();
        String word3 = user.getNickname();
        String word4 = cashOrder.getPlatformOrderSet().stream().findAny().orElse(null).getId();
        PlatformWithdrawalOrder word5 = cashOrder.getPlatformWithdrawalOrderSet().stream().findAny().orElse(null);

        assertThat(orderService.queryUserOrders(word1))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word2))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word3))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word4))
                .contains(cashOrder);
        if (word5 != null)
            assertThat(orderService.queryUserOrders(word5.getId()))
                    .contains(cashOrder);
        // 试试提现订单了!


        final WithdrawOrder withdrawOrder = withdrawOrderRepository.getOne(makeFinishWithdrawOrder(user, randomOrderAmount()).getId());
        String word7 = withdrawOrder.getId();
        String word8 = withdrawOrder.getFriendlyId();
        String word9 = user.getNickname();
        PlatformWithdrawalOrder word10 = withdrawOrder.getPlatformWithdrawalOrderSet().stream().findAny().orElse(null);

        assertThat(orderService.queryUserOrders(word7))
                .contains(withdrawOrder);
        assertThat(orderService.queryUserOrders(word8))
                .contains(withdrawOrder);
        if (word10 != null)
            assertThat(orderService.queryUserOrders(word10.getId()))
                    .contains(withdrawOrder);
        assertThat(orderService.queryUserOrders(word9))
                .contains(withdrawOrder, cashOrder);

        driver.get("http://localhost/manage/order");
        ManageOrderPage orderPage = initPage(ManageOrderPage.class);

        orderPage.search("");
        orderPage = initPage(ManageOrderPage.class);

        orderPage.search(word1);
//        System.out.println(driver.getPageSource());

        CashOrder needTradeOrder = orderService.newOrder(user, randomOrderAmount(), UUID.randomUUID().toString(), user.getCards().get(0).getId());
        orderService.preparePay(needTradeOrder.getId(), PayChannel.weixin);
        assertThat(needTradeOrder.isCompleted())
                .isFalse();
        CashOrder needWithOrder = orderService.newOrder(user, randomOrderAmount(), UUID.randomUUID().toString(), user.getCards().get(0).getId());
        tradeSuccess(needWithOrder);
        assertThat(needWithOrder.isWithdrawalCompleted())
                .isFalse();

        driver.navigate().back();
        orderPage = initPage(ManageOrderPage.class);
        orderPage.search(word9);
//        System.out.println(driver.getPageSource());

        // 分别执行一次全审核和订单审核
        // 思考下 如何确定该状态已经被更新过?
        // 显然我们需要建立一个订单 这个订单的支付是没有完成的
        // 另外需要一个订单  它的支付是完成的,提现也进行了 但是没有完成
        ManageOrderResultPage resultPage = initPage(ManageOrderResultPage.class);
        resultPage.platformCheckAll();
        // 完成之后这2个订单都应该被处理了
        needTradeOrder = orderService.getOne(needTradeOrder.getId());
        assertThat(needTradeOrder.isCompleted())
                .isTrue();
        //并且等待支付了
        assertThat(needTradeOrder.getPlatformWithdrawalOrderSet())
                .isNotEmpty();
        needWithOrder = orderService.getOne(needWithOrder.getId());
        assertThat(needWithOrder.isWithdrawalCompleted())
                .isTrue();
        // 再执行一个单独的订单
        needTradeOrder = orderService.newOrder(user, randomOrderAmount(), UUID.randomUUID().toString(), user.getCards().get(0).getId());
        orderService.preparePay(needTradeOrder.getId(), PayChannel.weixin);
        assertThat(needTradeOrder.isCompleted())
                .isFalse();
        resultPage = resultPage.searchAgain(needTradeOrder.getFriendlyId());
        resultPage.platFormCheckOrder(needTradeOrder.getId());
        needTradeOrder = orderService.getOne(needTradeOrder.getId());
        assertThat(needTradeOrder.isCompleted())
                .isTrue();
    }

}