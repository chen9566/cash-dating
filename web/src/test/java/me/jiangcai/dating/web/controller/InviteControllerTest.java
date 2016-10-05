package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.MyInviteCodePage;
import me.jiangcai.dating.page.MyInvitePage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class InviteControllerTest extends LoginWebTest {

    @Autowired
    private StatisticService statisticService;

    @Test
    public void invite() {
        driver.get("http://localhost/my");
        MyPage page = initPage(MyPage.class);


        page.clickMenu("我的邀请");
        invite(initPage(MyInvitePage.class));
        driver.get("http://localhost/my");
        page.reloadPageInfo();
    }


    private void invite(MyInvitePage page) {
        page.assertUser(currentUser(),statisticService);

        // TODO 提现

        page.clickMyCode();
        MyInviteCodePage codePage = initPage(MyInviteCodePage.class);


    }

}