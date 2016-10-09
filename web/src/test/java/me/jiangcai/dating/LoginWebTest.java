package me.jiangcai.dating;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.BankService;
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
import java.util.UUID;

/**
 * 已登录的测试
 *
 * @author CJ
 */
@ContextConfiguration(classes = LoginWebTest.Config.class)
public abstract class LoginWebTest extends WebTest {
    protected static WeixinUserDetail detail;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private BankService bankService;

    protected List<CashOrder> currentOrders() {
        return orderService.findOrders(detail.getOpenId());
    }

    protected User currentUser() {
        return userService.byOpenId(detail.getOpenId());
    }

    protected String randomBankCard() {
        return RandomStringUtils.randomNumeric(16);
    }

    @Before
    public void forLogin() {
        detail = WeixinUserMocker.randomWeixinUserDetail();
        String mobile = randomMobile();
        verificationCodeService.sendCode(mobile, VerificationType.register);
        userService.registerMobile(null, detail.getOpenId(), mobile, "1234", null);
//        verificationCodeService.sendCode(mobile, Function.identity()); 现在不用发验证码了
        // 16
        String card = randomBankCard();
        userService.addCard(detail.getOpenId(), detail.getNickname(), card
                , bankService.list().stream()
                        .findAny().orElse(null), null, UUID.randomUUID().toString());
    }

    static class Config {
        @Bean
        @Primary
        public WeixinUserMocker weixinUserMocker() {
            return (modelAndViewContainer, nativeWebRequest) -> detail;
        }
    }
}
