package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 人物关系的测试
 *
 * @author CJ
 */
public class RelationTest extends WebTest {

    @Test
    public void tryIt() throws IOException {
        // 建立一个人物 并且获取它的信息
        User invite = helloNewUser(null);

        assertThat(invite.getGuideUser())
                .isNull();

        // 拿出他的分享链接
        String url = currentUserInviteURL();

        // 新用户来吧
        driver.manage().deleteAllCookies();
        driver.quit();
        createWebDriver();
        driver.get(url);// 这个时候发生了什么事? 为什么还是原来的用户?
        User newOne = helloNewUser(null);
        assertThat(newOne.getGuideUser())
                .isEqualTo(invite);
    }
}
