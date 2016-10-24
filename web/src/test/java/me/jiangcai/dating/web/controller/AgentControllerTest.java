package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.BookRateLevel;
import me.jiangcai.dating.page.MyMoneyPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.MyTeamPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试合伙人相关
 *
 * @author CJ
 */
public class AgentControllerTest extends LoginWebTest {

    @Autowired
    private AgentService agentService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void index() {
        User currentUser = currentUser();
        agentService.makeAgent(currentUser);

        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);
        myPage.clickMenu("我的邀请");
        MyMoneyPage invitePage = initPage(MyMoneyPage.class);
        invitePage.assertTeam();

        invitePage.clickMyTeam();

        MyTeamPage teamPage = initPage(MyTeamPage.class);

        teamPage.assertTeamSize(0);

        // 然后我们获得一个成员
        User user = createMember(currentUser);
        assertThat(user.getAgentUser())
                .isEqualTo(currentUser);
        teamPage.refresh();
        teamPage.assertMember(0, user);

        // 我们更改一个等级 再下一个单 检查下手续费
        BookRateLevel level = BookRateLevel.values()[random.nextInt(BookRateLevel.values().length)];
        while (level == BookRateLevel.threshold)
            level = BookRateLevel.values()[random.nextInt(BookRateLevel.values().length)];
        teamPage.changeLevel(0, level);

        user = userService.by(user.getId());
        CashOrder order = orderService.newOrder(user, new BigDecimal("100"), UUID.randomUUID().toString(), null);
        assertThat(order.getThatRateConfig().getBookRate())
                .isEqualByComparingTo(level.toRate());
    }

    private User createMember(User owner) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        CashFilter.makeRequestBelongTo(request, owner.getId());

        User user = userService.newUser(UUID.randomUUID().toString().replaceAll("-", ""), request);
        user.setNickname(randomString(7));
        user.setMobileNumber(randomMobile());
        return userRepository.save(user);
    }

}