package me.jiangcai.dating.web.controller.my;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.LogoutPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.SystemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的 相关测试
 *
 * @author CJ
 */
public class MyControllerTest extends WebTest {

    @Autowired
    private SystemService systemService;

    @Test
    public void checkLinks() throws IOException {
        helloNewUser(null, true);
        MyPage myPage = myPage();
        // 检查各个功能完整性
        // 这个版本开始 从my 可以进入start
        myPage.clickMenu("收银台");
        initPage(StartOrderPage.class);
        myPage = myPage();
        myPage.clickMenu("款爷办卡");
        // 此时它的地址应该是跟系统属性中的办卡地址一致的
        System.out.println(driver.getTitle());  // 融360
        assertThat(driver.getCurrentUrl())
                .isEqualTo(systemService.getApplyCardUrl());
    }

    @Test
    public void flow() throws IOException {
        helloNewUser(null, true);
        MyPage myPage = myPage();

        LogoutPage page = myPage.logout();
        page.assertContain(getSystemService().getPublicAccountName());
        page.loginAgain();

        assertThat(driver.getTitle())
                .isEqualTo("注册");
    }

}