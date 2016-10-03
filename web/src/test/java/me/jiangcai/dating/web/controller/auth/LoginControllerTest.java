package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.BindingMobilePage;
import org.junit.Test;

/**
 * @author CJ
 */
public class LoginControllerTest extends WebTest{

    @Test
    public void newUser(){
        driver.get("http://localhost/");
        BindingMobilePage page = initPage(BindingMobilePage.class);

        page.submitWithNothing();
        page.inputMobileNumber(randomMobile());
        page.sendCode();
        // 找到最近发送的验证码
    }

}