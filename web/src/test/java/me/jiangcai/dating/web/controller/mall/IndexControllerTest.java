package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.LoginPage;
import me.jiangcai.dating.page.mall.RegisterPage;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class IndexControllerTest extends WebTest {

    /**
     * 公众号先行注册然后来到PC
     */
    @Test
    public void wxFirst() throws Exception {
        User user = helloNewUser(null, true);

        registerOnMall(user.getMobileNumber());
    }

    /**
     * PC先行注册然后再来到公众号
     */
    @Test
    public void pcFirst() throws Exception {

        String mobile = registerOnMall(null);

        User user = helloNewUser(null, true, mobile);

        assertThat(userService.by(user.getId()).getMobileNumber())
                .isEqualTo(mobile);
    }

    private String registerOnMall(String mobile) throws Exception {
        WebDriver driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();

        mockMvc.perform(get("/mall/"))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        ;

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class, driver);

//        indexPage.printThisPage();

        indexPage.assertNotLogin();

        // 现在打开注册页面
        RegisterPage registerPage = indexPage.openRegisterPage();

        // 填写 手机号码，然后点击验证码，填入验证码，密码，确认密码
        if (mobile == null)
            mobile = randomMobile();
        String password = randomMobile();
        registerPage.registerAsRandom(mobile, password);

//        System.out.println(driver.getPageSource());
        // 开始进入登录流程
        indexPage = initPage(IndexPage.class, driver);
        LoginPage loginPage = indexPage.openLoginPage();

        indexPage = loginPage.loginAs(mobile, password);

//        System.out.println(driver.getPageSource());
        indexPage.assertLogin();

        return mobile;
    }

}