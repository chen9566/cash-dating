package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.AgentRequestPage;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyInvitationPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 合伙人已经从现有需求中排除
 *
 * @author CJ
 */
@Ignore
public class AgentTest extends LoginWebTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private AgentService agentService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void invite() throws IOException, SignatureException {
        driver.get("http://localhost/my");
        MyPage page = initPage(MyPage.class);
        page.clickMenu("合伙赚钱");
        CodePage codePage1 = initPage(CodePage.class);
        invite(codePage1.toMyInvitationPage(), true);
        driver.get("http://localhost/my");
        page.reloadPageInfo();

        driver.get("http://localhost/myInviteCode");
        CodePage codePage = initPage(CodePage.class);

        codePage.assertUser(currentUser(), qrCodeService);

        // 这里已经申请过了 所有应该先删除
        User user = currentUser();
        agentService.waitingList().stream()
                .filter(agentRequest -> agentRequest.getFrom().equals(user))
                .forEach(
                        agentRequest -> agentService.declineRequest(null, agentRequest.getId(), UUID.randomUUID().toString())
                );
        doRequestAgent(codePage.requestAgent());

        //现在同意你的申请
        agentService.waitingList().stream()
                .filter(agentRequest -> agentRequest.getFrom().equals(user))
                .forEach(
                        agentRequest -> agentService.approveRequest(null, agentRequest.getId(), UUID.randomUUID().toString())
                );

        User myUser = userService.by(user.getId());
        assertThat(myUser.getAgentInfo())
                .isNotNull();
        // 添加新用户
        User newbie = userService.byOpenId(createNewUser(myUser).getOpenId());
        newbie.setNickname(randomString(10));
        newbie = userRepository.save(newbie);
        // 刷订单
        createSuccessCashOrder(newbie, "200");
        createSuccessCashOrder(newbie, "100");

        addUserBalance(myUser.getOpenId(), randomOrderAmount());

        // 我再提现
        BigDecimal balance = statisticService.balance(myUser.getOpenId());
        orderService.newWithdrawOrder(myUser, balance, null);

        driver.get("http://localhost/my");
        page = initPage(MyPage.class);
        page.clickMenu("合伙赚钱");
        codePage1 = initPage(CodePage.class);
        invite(codePage1.toMyInvitationPage(), false);
    }

    private void createSuccessCashOrder(User user, String amount) throws IOException, SignatureException {
        CashOrder cashOrder = orderService.newOrder(user, new BigDecimal(amount), UUID.randomUUID().toString(), null);
        tradeSuccess(cashOrder);
    }


    private void invite(MyInvitationPage page, boolean ableRequest) throws IOException {
        page.assertUser(currentUser(), statisticService);
        if (ableRequest) {
            doRequestAgent(page.toRequestAgentPage());
        } else {
//            page.
            // 这里的话 页面依然还是
            page.reloadPageInfo();

        }
    }

    private void doRequestAgent(AgentRequestPage requestPage) {
//        AgentRequestPage requestPage = initPage(AgentRequestPage.class);

        final String mobile = randomMobile();
        requestPage.submitRequest(currentUser().getNickname(), mobile);

        final AgentRequest agentRequest1 = agentService.waitingList().stream()
                .filter(agentRequest -> agentRequest.getFrom().equals(currentUser()))
                .findFirst()
                .orElseThrow(AssertionError::new);

        assertThat(agentRequest1.getMobileNumber())
                .isEqualTo(mobile);

        // 这里动点坏脑子, 检查下错误的示范
        driver.get("http://localhost/agent");
        requestPage = initPage(AgentRequestPage.class);
        requestPage.submitRequest(currentUser().getNickname(), mobile);
        // 这里应该存在一个alert
        Alert alert = driver.switchTo().alert();
//        System.out.println(alert.getText());
    }

}