package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.StartPayPage;
import org.junit.Test;

/**
 * 已登录的
 *
 * @author CJ
 */
public class HomeControllerTest extends LoginWebTest {

    @Test
    public void index() {
        driver.get("http://localhost/");

        StartPayPage page = initPage(StartPayPage.class);

        int amount = Math.abs(random.nextInt());

        page.pay(amount,"");

        // 这个时候应该是到了二维码界面,在这个界面 我们可以分享它

    }

}