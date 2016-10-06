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
@ActiveProfiles({"test","unit_test"})
public class LoginControllerTest extends WebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;
    @Autowired
    private QRCodeService qrCodeService;

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
        //
        // 地址自己选吧

        cardPage.submitWithRandomAddress(bankService.list().stream()
                .findAny().orElse(null), RandomStringUtils.randomAlphanumeric(3), RandomStringUtils.randomAlphanumeric(6), RandomStringUtils.randomNumeric(16));
        initPage(StartOrderPage.class);
        // 这就对了!
        // 还需要检查 银行是否已设置 地址是否已设置
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);
        myPage.clickMenu("我的邀请");
        MyInvitePage invitePage = initPage(MyInvitePage.class);
        invitePage.clickMyCode();
        MyInviteCodePage codePage = initPage(MyInviteCodePage.class);
        String url = qrCodeService.scanImage(codePage.getQRCodeImage());
        //终于找到id了
        Long userId = CashFilter.guideUserFromURL(url);

        User user = userRepository.getOne(userId);

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