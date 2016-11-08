package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.FinancingPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.service.WealthService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class WealthControllerTest extends LoginWebTest {

    @Autowired
    private WealthService wealthService;

    @Test
    public void financing() throws Exception {
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        FinancingPage financingPage = myPage.toFinancingPage();

        financingPage.assertFinancing(wealthService.currentFinancing());
        // 我这边点击 肯定是会提示让我输入验证码
        financingPage.goFinancing();
    }

}