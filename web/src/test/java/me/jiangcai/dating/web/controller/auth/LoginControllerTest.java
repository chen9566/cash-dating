package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.BindingMobilePage;
import me.jiangcai.dating.page.MyInviteCodePage;
import me.jiangcai.dating.page.MyInvitePage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.web.mvc.CashFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

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