package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class LoginControllerTest extends WebTest {

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
    public void newUser() throws IOException {
        driver.manage().deleteAllCookies();

        User user = helloNewUser();

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


    }

}