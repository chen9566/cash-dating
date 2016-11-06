package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyInvitationPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 人物关系的测试
 *
 * @author CJ
 */
public class RelationTest extends WebTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private AgentService agentService;

    @Test
    public void tryIt() throws IOException, SignatureException {
        // 建立一个人物 并且获取它的信息
        User invite = helloNewUser(null, true);

        agentService.makeAgent(invite);

        assertThat(invite.getGuideUser())
                .isNull();

        // 拿出他的分享链接
        String url = currentUserInviteURL();

        // 新用户来吧
//        driver.manage().deleteAllCookies();
//        driver.quit();
        WebDriver oldDriver = driver;
        createWebDriver();
//        driver.get(url);// 这个时候发生了什么事? 为什么还是原来的用户?
        User newOne = helloNewUser(url, invite, true);
        assertThat(newOne.getGuideUser())
                .isEqualTo(invite);

        // 新用户也可以看
        noTeamPage();


        CashOrder order = orderService.newOrder(newOne, new BigDecimal(100), UUID.randomUUID().toString(), newOne.getCards().get(0).getId());
        tradeSuccess(order);

        // 老用户看流水
        oldDriver.get("http://localhost/my");
        MyPage page = PageFactory.initElements(oldDriver, MyPage.class);
        page.validatePage();
        page.clickMenu("合伙赚钱");
        CodePage codePage = PageFactory.initElements(oldDriver, CodePage.class);
        codePage.setTestInstance(this);
        codePage.validatePage();
        MyInvitationPage myInvitationPage = codePage.toMyInvitationPage();
        myInvitationPage.validatePage();
        myInvitationPage.assertTeam();

        myInvitationPage.assertUser(invite, statisticService);

        // 这里再次浏览 应该是My
        driver.get(url);
        initPage(MyPage.class);
    }

    private void noTeamPage() {
        driver.get("http://localhost/my");
        MyPage page = initPage(MyPage.class);
        page.clickMenu("合伙赚钱");
        CodePage codePage = initPage(CodePage.class);
        MyInvitationPage invitationPage = codePage.toMyInvitationPage();
        invitationPage.assertNoTeam();
    }
}
