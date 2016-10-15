package me.jiangcai.dating.web.controller.auth;

import com.google.common.base.Predicate;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.AgentInfo;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.LogoutPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.PCLoginPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class LoginControllerTest extends WebTest {

    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private AgentService agentService;

    @Test
    public void pcLogin() throws IOException, InterruptedException {
        // 需要一个新的driver实例
        WebDriver pcDriver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                .build();

        pcDriver.get("http://localhost/");


        WebDriverWait webDriverWait = new WebDriverWait(pcDriver, 5);
        webDriverWait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                PCLoginPage loginPage = PageFactory.initElements(input, PCLoginPage.class);
                loginPage.validatePage();
                try {
                    loginPage.codeImage();
                    return true;
                } catch (Exception ex) {
                    return false;
                }

            }
        });

        PCLoginPage loginPage = PageFactory.initElements(pcDriver, PCLoginPage.class);
        loginPage.validatePage();


        String url = qrCodeService.scanImage(loginPage.codeImage());

        // 好了 一会儿让我们的
        helloNewUser(url, null, true);

        driver.get(url);
        // 好了 关注我们的pcDriver
//        Thread.sleep(1000);

//        pcDriver.get("http://localhost/");
//        System.out.println(pcDriver.getPageSource());
        webDriverWait = new WebDriverWait(pcDriver, 20);
        webDriverWait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                try {
                    StartOrderPage page = PageFactory.initElements(input, StartOrderPage.class);
                    // 应该没有管理权 所以是这个
                    page.validatePage();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
        StartOrderPage page = PageFactory.initElements(pcDriver, StartOrderPage.class);
        // 应该没有管理权 所以是这个
        page.validatePage();

//        driver.get("http://localhost/start");
//        System.out.println(driver.getPageSource());
    }

    @Test
    public void sendCode() throws Exception {
        mockMvc.perform(
                put("/verificationCode")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("mobile", randomMobile())
                        .param("type", "register")
//                        .content("type=register&mobile=" + randomMobile()) ??
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void logout() throws IOException {
        // 登录用户
        // 回头见
        driver.manage().deleteAllCookies();

        User user = helloNewUser(null, true);

        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        myPage.logout();
        LogoutPage page = initPage(LogoutPage.class);
        System.out.println(page);
    }

    @Test
    public void newUser() throws IOException {
        driver.manage().deleteAllCookies();

        User user = helloNewUser(null, true);

        assertThat(user.getCards())
                .isNotEmpty();
        Card card = user.getCards().get(0);

//        assertThat(card.getAddress())
//                .isNotNull();
//        assertThat(card.getAddress().getCity())
//                .isNotEmpty();
//        assertThat(card.getAddress().getProvince())
//                .isNotEmpty();
        assertThat(card.getBank())
                .isNotNull();
        assertThat(card.getOwner())
                .isNotEmpty();
        assertThat(card.getSubBranch())
                .isNotEmpty();
        assertThat(card.getNumber())
                .isNotEmpty();

        // 测试邀请码
        User boss = userService.byOpenId(createNewUser().getOpenId());
        driver.quit();
        createWebDriver();
        User newUser = helloNewUser(boss.getInviteCode(), true);
        assertThat(newUser.getGuideUser())
                .isEqualTo(boss);
        assertThat(newUser.getAgentUser())
                .isNull();
        // 如果当时他是合伙人
        AgentInfo info = agentService.makeAgent(boss);
        driver.quit();
        createWebDriver();
        newUser = helloNewUser(boss.getInviteCode(), true);
        assertThat(newUser.getGuideUser())
                .isEqualTo(boss);
        assertThat(newUser.getAgentUser())
                .isEqualTo(boss);

    }

}