package me.jiangcai.dating;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Predicate;
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
import me.jiangcai.dating.web.WebConfig;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.lib.test.page.AbstractPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.WebConnectionHtmlUnitDriver;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles({"test", "unit_test"})
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebConfig.class})
public abstract class WebTest extends SpringWebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;
    @Autowired
    private QRCodeService qrCodeService;

    @Override
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        try {
            return super.initPage(clazz);
        } catch (RuntimeException ex) {
            System.out.println(driver.getCurrentUrl());
            System.out.println(driver.getPageSource());
            throw ex;
        }
    }

    @Override
    protected DefaultMockMvcBuilder buildMockMVC(DefaultMockMvcBuilder builder) {
        return builder.addFilters(new CashFilter());
//        return super.buildMockMVC(builder);
    }

    protected MockHttpServletRequestBuilder getWeixin(String urlTemplate, Object... urlVariables) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate, urlVariables);
        builder.header("user-agent", "MicroMessenger");
        return builder;
    }


    @Override
    protected void createWebDriver() {
        driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
//                .useMockMvcForHosts("wxtest.jiangcai.me")
                .withDelegate(new WebConnectionHtmlUnitDriver() {
                    @Override
                    protected WebClient modifyWebClientInternal(WebClient webClient) {
                        webClient.addRequestHeader("user-agent", "MicroMessenger");
                        return super.modifyWebClientInternal(webClient);
                    }
                })
                // DIY by interface.
                .build();
    }


    /**
     * 磨磨唧唧的建立一个新用户
     *
     * @return
     * @throws IOException
     */
    protected User helloNewUser() throws IOException {
        final String startUrl = "http://localhost/start";
        return helloNewUser(startUrl);
    }

    /**
     * 磨磨唧唧的建立一个新用户
     *
     * @return
     * @throws IOException
     */
    protected User helloNewUser(String startUrl) throws IOException {
        driver.get(startUrl);
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
        String url = currentUserInviteURL();
        //终于找到id了
        Long userId = CashFilter.guideUserFromURL(url);

        return userRepository.getOne(userId);
    }

    /**
     * @return 当前登录用户的邀请链接
     * @throws IOException
     */
    protected String currentUserInviteURL() throws IOException {
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);
        myPage.clickMenu("我的邀请");
        MyInvitePage invitePage = initPage(MyInvitePage.class);
        invitePage.clickMyCode();
        MyInviteCodePage codePage = initPage(MyInviteCodePage.class);

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                try {
                    codePage.reloadPageInfo();
                    codePage.getQRCodeImage();
                    return true;
                } catch (IOException e) {
                    throw new InternalError(e);
                } catch (IllegalArgumentException ex) {
                    //
                }
                return false;
            }
        });

        try {
            return qrCodeService.scanImage(codePage.getQRCodeImage());
        } catch (IllegalArgumentException exception) {
            codePage.printThisPage();
            throw exception;
        }
    }
}
