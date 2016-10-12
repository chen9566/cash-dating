package me.jiangcai.dating;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

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
    private BankService bankService;

    protected List<CashOrder> currentOrders() {
        return orderService.findOrders(detail.getOpenId());
    }

    protected User currentUser() {
        return userService.byOpenId(detail.getOpenId());
    }

    @Before
    public void forLogin() {
        detail = createNewUser();
    }

    static class Config {
        @Bean
        @Primary
        public WeixinUserMocker weixinUserMocker() {
            return (modelAndViewContainer, nativeWebRequest) -> detail;
        }
    }
}
