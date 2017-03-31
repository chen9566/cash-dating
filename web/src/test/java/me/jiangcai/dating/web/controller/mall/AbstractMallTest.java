package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.mall.AbstractMallPage;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.LoginPage;
import me.jiangcai.dating.page.mall.RegisterPage;
import org.openqa.selenium.WebDriver;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;

/**
 * @author CJ
 */
abstract class AbstractMallTest extends WebTest {

    /**
     * 注册
     *
     * @param mobile   手机号码
     * @param mallPage 可以注册的页面
     * @return
     * @throws Exception
     */
    String registerOnMall(String mobile, AbstractMallPage mallPage) throws Exception {
        // 现在打开注册页面
        RegisterPage registerPage = mallPage.openRegisterPage();

        // 填写 手机号码，然后点击验证码，填入验证码，密码，确认密码
        if (mobile == null)
            mobile = randomMobile();
        String password = randomMobile();
        registerPage.registerAsRandom(mobile, password);

//        System.out.println(driver.getPageSource());
        // 开始进入登录流程
        IndexPage indexPage = initPage(IndexPage.class, mallPage.getWebDriver());
        LoginPage loginPage = indexPage.openLoginPage();

        AbstractMallPage successPage = loginPage.loginAs(mobile, password);

//        System.out.println(driver.getPageSource());
        successPage.assertLogin();

        return mobile;
    }

    String registerOnMall(String mobile) throws Exception {
        WebDriver driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();

//        mockMvc.perform(get("/mall/"))
////                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//        ;

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class, driver);

//        indexPage.printThisPage();

        indexPage.assertNotLogin();

        return registerOnMall(mobile, indexPage);
    }
}
