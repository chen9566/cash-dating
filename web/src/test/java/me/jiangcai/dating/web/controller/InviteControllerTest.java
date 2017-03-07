package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.model.InviteLevel;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyInvitationPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 推广激励
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
    public void go() throws IOException, SignatureException {
        // 一个新用户
        // 然后慢慢邀请用户 并且让用户刷卡 然后它的等级也满满变高
        String oneNewUserOpenId = currentUser().getOpenId();
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.threshold);
        createValidUserFor(oneNewUserOpenId);
        createValidUserFor(oneNewUserOpenId);
        createValidUserFor(oneNewUserOpenId);
        createValidUserFor(oneNewUserOpenId);
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.threshold);
        createValidUserFor(oneNewUserOpenId);
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.senior);
        // 接下来5个订单 获得 senior 奖励
        BigDecimal senior = BigDecimal.ZERO;
        senior = senior.add(createValidUserFor(oneNewUserOpenId));
        senior = senior.add(createValidUserFor(oneNewUserOpenId));
        senior = senior.add(createValidUserFor(oneNewUserOpenId));
        senior = senior.add(createValidUserFor(oneNewUserOpenId));
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.senior);
        senior = senior.add(createValidUserFor(oneNewUserOpenId));
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.expert);

        // 接下来5个订单获得 expert 奖励
        BigDecimal expert = BigDecimal.ZERO;
        expert = expert.add(createValidUserFor(oneNewUserOpenId));
        expert = expert.add(createValidUserFor(oneNewUserOpenId));
        expert = expert.add(createValidUserFor(oneNewUserOpenId));
        expert = expert.add(createValidUserFor(oneNewUserOpenId));
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.expert);
        expert = expert.add(createValidUserFor(oneNewUserOpenId));
        myPage().toCodePage().toMyInvitationPage().assertInviteLevel(InviteLevel.best);

        BigDecimal best = createValidUserFor(oneNewUserOpenId);

        myPage().toCodePage().toMyInvitationPage().toWithdrawPage().assertBalance(
                senior.multiply(InviteLevel.senior.getCommissionRate())
                        .add(
                                expert.multiply(InviteLevel.expert.getCommissionRate())
                        )
                        .add(
                                best.multiply(InviteLevel.best.getCommissionRate())
                        )
        );

        myPage().toCodePage().toMyInvitationPage().printThisPage();
    }

    @Ignore
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

    private BigDecimal createValidUserFor(String openId) throws IOException, SignatureException {
        return createValidUserFor(openId, randomOrderAmount());
    }

    private BigDecimal createValidUserFor(String openId, BigDecimal amount) throws IOException, SignatureException {
        String newOpenId = createNewUser(userService.byOpenId(openId)).getOpenId();
        makeFinishCashOrder(userService.byOpenId(newOpenId), amount, null);
        return amount;
    }


}