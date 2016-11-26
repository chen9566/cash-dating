package me.jiangcai.dating.web.controller.my;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyInvitationPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.WithdrawListPage;
import me.jiangcai.dating.page.WithdrawPage;
import me.jiangcai.dating.page.WithdrawResultPage;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.StatisticService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 提现测试
 *
 * @author CJ
 */
public class WithdrawControllerTest extends LoginWebTest {

    @Autowired
    private AgentService agentService;
    @Autowired
    private StatisticService statisticService;

    @Test
    public void flow() throws IOException, SignatureException {
        // 先删除卡
        cardService.deleteCards(currentUser().getOpenId());

        // 去我的
        MyPage myPage = myPage();
        //
        myPage.clickMenu("合伙赚钱");
        CodePage codePage = initPage(CodePage.class);
        // 去我的邀请
        MyInvitationPage myInvitationPage = codePage.toMyInvitationPage();
        myInvitationPage.assertNoTeam();
        // 先让自己成为代理商
        agentService.makeAgent(currentUser());
        myInvitationPage.refresh();
        myInvitationPage.assertTeam();

        WithdrawPage page;
        try {
            page = myInvitationPage.toWithdrawPage();
            assertThat(false)
                    .as("应该看到绑卡页面")
                    .isTrue();
        } catch (ComparisonFailure failure) {
            // 应该是到了银行卡页面
            SubBranchBank subBranchBank = randomSubBranchBank();

            final String owner = RandomStringUtils.randomAlphanumeric(3);
            final String number = randomBankCard();
            bindCard(currentUser().getMobileNumber(), subBranchBank, owner, number);
            page = initPage(WithdrawPage.class);
        }

        // 发现没钱~
        page.assertBalance(BigDecimal.ZERO);

        // 刷钱
        makeBalance();

        // 检查下呗
        BigDecimal balance = statisticService.balance(currentUser().getOpenId());
        System.out.println(balance);
        assertThat(balance)
                .isGreaterThan(BigDecimal.ZERO);

        // 刷他100
        page.refresh();
        page.assertBalance(balance);

        WithdrawResultPage resultPage = page.withdraw(balance);

//        WithdrawListPage listPage = page.withdraw(balance);
        WithdrawListPage listPage = resultPage.back();

        // 即可看到提现的历史了
        listPage.assertList(statisticService.withdrawalFlows(currentUser().getOpenId()));

        assertThat(statisticService.balance(currentUser().getOpenId()))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    private void makeBalance() throws IOException, SignatureException {
        User newbie = userService.byOpenId(createNewUser(currentUser()).getOpenId());
        CashOrder cashOrder = orderService.newOrder(newbie, new BigDecimal("100000"), null, null);
        tradeSuccess(cashOrder);
        withdrawalSuccess(cashOrder);
    }

}