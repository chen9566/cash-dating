package me.jiangcai.dating.web.controller.my;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.LogoutPage;
import me.jiangcai.dating.page.MyPage;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的 相关测试
 *
 * @author CJ
 */
public class MyControllerTest extends WebTest {

    @Test
    public void flow() throws IOException {
        helloNewUser(null, true);
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        LogoutPage page = myPage.logout();
        page.assertContain(getSystemService().getPublicAccountName());
        page.loginAgain();

        assertThat(driver.getTitle())
                .isEqualTo("注册");
    }

}