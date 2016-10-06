package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.page.AgentRequestPage;
import me.jiangcai.dating.page.MyInviteCodePage;
import me.jiangcai.dating.page.MyInvitePage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class InviteControllerTest extends LoginWebTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private AgentService agentService;

    @Test
    public void invite() throws IOException {
        driver.get("http://localhost/my");
        MyPage page = initPage(MyPage.class);


        page.clickMenu("我的邀请");
        invite(initPage(MyInvitePage.class));
        driver.get("http://localhost/my");
        page.reloadPageInfo();
    }


    private void invite(MyInvitePage page) throws IOException {
        page.assertUser(currentUser(), statisticService);

        // TODO 提现

        page.clickMyCode();
        MyInviteCodePage codePage = initPage(MyInviteCodePage.class);

        codePage.assertUser(currentUser(), qrCodeService);

        // TODO 显然还有已经申请了代理商的呢?
        codePage.requestAgent();
        AgentRequestPage requestPage = initPage(AgentRequestPage.class);

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
        System.out.println(alert.getText());
    }

}