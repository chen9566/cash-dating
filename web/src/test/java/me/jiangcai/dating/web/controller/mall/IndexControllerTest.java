package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.LoginPage;
import me.jiangcai.dating.page.mall.RegisterPage;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class IndexControllerTest extends WebTest {

    @Test
    public void home() throws Exception {
        mockMvc.perform(get("/mall/"))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        ;

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

//        indexPage.printThisPage();

        indexPage.assertNotLogin();

        // 现在打开注册页面
        RegisterPage registerPage = indexPage.openRegisterPage();

        // 填写 手机号码，然后点击验证码，填入验证码，密码，确认密码
        String mobile = randomMobile();
        String password = randomMobile();
        registerPage.registerAsRandom(mobile, password);

//        System.out.println(driver.getPageSource());
        // 开始进入登录流程
        indexPage = initPage(IndexPage.class);
        LoginPage loginPage = indexPage.openLoginPage();

        indexPage = loginPage.loginAs(mobile, password);

//        System.out.println(driver.getPageSource());
        indexPage.assertLogin();
    }

}