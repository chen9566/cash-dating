package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.BindingMobilePage;
import me.jiangcai.dating.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class LoginControllerTest extends WebTest {

    @Autowired
    private UserService userService;

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
    public void newUser() {
        driver.get("http://localhost/");
        String mobile = randomMobile();
        BindingMobilePage page = initPage(BindingMobilePage.class);

        page.submitWithNothing();
        page.inputMobileNumber(mobile);
        page.sendCode();
        // 找到最近发送的验证码
        page.submitWithCode();

        // 应该到了下一个页面了

        BindingCardPage cardPage = initPage(BindingCardPage.class);
        // 这个用户已经产生
        assertThat(userService.byMobile(mobile))
                .isNotNull();
    }

}