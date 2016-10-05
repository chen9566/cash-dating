package me.jiangcai.dating;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.function.Function;

/**
 * 已登录的测试
 *
 * @author CJ
 */
@ContextConfiguration(classes = LoginWebTest.Config.class)
public abstract class LoginWebTest extends WebTest {
    @Autowired
    private OrderService orderService;

    protected List<CashOrder> currentOrders() {
        return orderService.findOrders(detail.getOpenId());
    }

    protected User currentUser(){
        return userService.byOpenId(detail.getOpenId());
    }

    static class Config {
        @Bean
        @Primary
        public WeixinUserMocker weixinUserMocker() {
            return (modelAndViewContainer, nativeWebRequest) -> detail;
        }
    }

    protected static WeixinUserDetail detail;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;

    protected String randomBankCard() {
        return RandomStringUtils.randomNumeric(16);
    }

    @Before
    public void forLogin() {
        detail = WeixinUserMocker.randomWeixinUserDetail();
        String mobile = randomMobile();
        verificationCodeService.sendCode(mobile, Function.identity());
        userService.registerMobile(detail.getOpenId(), mobile, "1234", null);
        verificationCodeService.sendCode(mobile, Function.identity());
        // 16
        String card = randomBankCard();
        userService.addCard(detail.getOpenId(), detail.getNickname(), card, "HZ", mobile, "1234");
    }
}
