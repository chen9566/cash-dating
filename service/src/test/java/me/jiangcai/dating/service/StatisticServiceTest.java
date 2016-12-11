package me.jiangcai.dating.service;

import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class StatisticServiceTest extends ServiceBaseTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AgentService agentService;

    @Test
    public void go() throws IOException, SignatureException {
        User user = userService.byOpenId(createNewUser().getOpenId());

        agentService.makeAgent(user);

        assertThat(statisticService.balance(user.getOpenId()))
                .isEqualTo("0");
        assertThat(statisticService.revenue(user.getOpenId()))
                .isEqualTo("0");
        assertThat(statisticService.totalExpense(user.getOpenId()))
                .isEqualTo("0");

        // 来一笔
        BigDecimal amount = randomBigDecimal();
        CashOrder cashOrder = orderService.newOrder(user, amount, null, null);
        tradeSuccess(cashOrder);

        assertThat(statisticService.balance(user.getOpenId()))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(statisticService.revenue(user.getOpenId()))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(statisticService.totalExpense(user.getOpenId()))
                .isEqualTo(amount);

        // 让队友来一笔
        User newbie = userService.byOpenId(createNewUser(user).getOpenId());
        cashOrder = orderService.newOrder(newbie, amount, null, null);
        tradeSuccess(cashOrder);

        // 手动给它加余额
        addUserBalance(user.getOpenId(), randomOrderAmount());

        assertThat(statisticService.balance(user.getOpenId()))
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(statisticService.revenue(user.getOpenId()))
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(statisticService.totalExpense(user.getOpenId()))
                .isEqualTo(amount);

        // 我提现
        WithdrawOrder withdrawOrder = orderService.newWithdrawOrder(user, statisticService.balance(user.getOpenId()), null);

        assertThat(statisticService.balance(user.getOpenId()))
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(statisticService.revenue(user.getOpenId()))
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(statisticService.totalExpense(user.getOpenId()))
                .isEqualTo(amount);

        // 提现失败
        withdrawalFailed(withdrawOrder, WithdrawalStatus.WITHDRAWAL_FAIL, "");

        assertThat(statisticService.balance(user.getOpenId()))
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(statisticService.revenue(user.getOpenId()))
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(statisticService.totalExpense(user.getOpenId()))
                .isEqualTo(amount);

        // 其他人支付呢? 这个算是收入吧
//        PayToUserOrder payToUserOrder = orderService.newPayToOrder(UUID.randomUUID().toString().replaceAll("-", ""), request, user, amount, UUID.randomUUID().toString());
//        tradeSuccess(payToUserOrder);
//
//        assertThat(statisticService.balance(user.getOpenId()))
//                .isGreaterThan(amount);
//        assertThat(statisticService.revenue(user.getOpenId()))
//                .isGreaterThan(amount);
//        assertThat(statisticService.totalExpense(user.getOpenId()))
//                .isEqualTo(amount);
    }

    private BigDecimal randomBigDecimal() {
        return new BigDecimal("100");
    }

}