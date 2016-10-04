package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
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
        System.out.println(driver.getPageSource());
    }

}