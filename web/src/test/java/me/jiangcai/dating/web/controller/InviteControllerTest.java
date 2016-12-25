package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyInvitationPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.SignatureException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 合伙人已经从现有需求中排除
 *
 * @author CJ
 */
public class InviteControllerTest extends LoginWebTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private AgentService agentService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void invite() throws IOException, SignatureException {
        String oneNewUserOpenId = createNewUser().getOpenId();
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(0);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(0);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isNotEqualByComparingTo(getSystemService().systemPreferentialRate());
        createValidUserFor(oneNewUserOpenId);
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(1);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(1);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(1);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isNotEqualByComparingTo(getSystemService().systemPreferentialRate());
        createValidUserFor(oneNewUserOpenId);
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(2);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(2);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(2);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isNotEqualByComparingTo(getSystemService().systemPreferentialRate());
        createValidUserFor(oneNewUserOpenId);
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(3);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(3);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(3);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isNotEqualByComparingTo(getSystemService().systemPreferentialRate());
        createValidUserFor(oneNewUserOpenId);
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(4);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(4);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(4);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isNotEqualByComparingTo(getSystemService().systemPreferentialRate());
        createValidUserFor(oneNewUserOpenId);
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(5);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(5);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(5);
        assertThat(getSystemService().systemBookRate(userService.byOpenId(oneNewUserOpenId)))
                .isEqualByComparingTo(getSystemService().systemPreferentialRate());

        createNewUser(userService.byOpenId(oneNewUserOpenId));
        assertThat(userService.validInvites(oneNewUserOpenId))
                .isEqualTo(5);
        assertThat(userService.validInviteUsers(oneNewUserOpenId))
                .hasSize(5);
        assertThat(userService.allInviteUsers(oneNewUserOpenId))
                .hasSize(6);
        /// 完成业务测试 再开始页面测试
        MyPage myPage = myPage();

        CodePage codePage = myPage.toCodePage();

        //随机构建些玩意儿
        int count = random.nextInt(10);
        while (count-- > 0) {
            if (random.nextBoolean())
                createValidUserFor(currentUser().getOpenId());
            else
                createNewUser(currentUser());
        }
//        codePage.printThisPage();
        MyInvitationPage invitationPage = codePage.toMyInvitationPage();
        invitationPage.assertUser(currentUser()
                , userRepository.findByMobileNumberNotNullAndGuideUser_OpenId(currentUser().getOpenId())
                , userService);
    }

    private void createValidUserFor(String openId) throws IOException, SignatureException {
        String newOpenId = createNewUser(userService.byOpenId(openId)).getOpenId();
        makeFinishCashOrder(userService.byOpenId(newOpenId), randomOrderAmount(), null);
    }


}