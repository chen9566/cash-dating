package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.page.ManageOrderPage;
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
        String word5 = cashOrder.getPlatformWithdrawalOrderSet().stream().findAny().orElse(null).getId();

        assertThat(orderService.queryUserOrders(word1))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word2))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word3))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word4))
                .contains(cashOrder);
        assertThat(orderService.queryUserOrders(word5))
                .contains(cashOrder);
        // 试试提现订单了!


        final WithdrawOrder withdrawOrder = withdrawOrderRepository.getOne(makeFinishWithdrawOrder(user, randomOrderAmount()).getId());
        String word7 = withdrawOrder.getId();
        String word8 = withdrawOrder.getFriendlyId();
        String word9 = user.getNickname();
        String word10 = withdrawOrder.getPlatformWithdrawalOrderSet().stream().findAny().orElse(null).getId();

        assertThat(orderService.queryUserOrders(word7))
                .contains(withdrawOrder);
        assertThat(orderService.queryUserOrders(word8))
                .contains(withdrawOrder);
        assertThat(orderService.queryUserOrders(word10))
                .contains(withdrawOrder);
        assertThat(orderService.queryUserOrders(word9))
                .contains(withdrawOrder, cashOrder);

        driver.get("http://localhost/manage/order");
        ManageOrderPage orderPage = initPage(ManageOrderPage.class);

        orderPage.search("");
        orderPage = initPage(ManageOrderPage.class);

        orderPage.search(word1);
        System.out.println(driver.getPageSource());
        driver.navigate().back();
        orderPage = initPage(ManageOrderPage.class);
        orderPage.search(word9);
        System.out.println(driver.getPageSource());

    }

}