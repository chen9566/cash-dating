package me.jiangcai.dating;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Predicate;
import me.jiangcai.chanpay.test.mock.MockPay;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.BindingMobilePage;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.web.WebConfig;
import me.jiangcai.lib.test.page.AbstractPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.WebConnectionHtmlUnitDriver;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles({"test", "unit_test"})
@WebAppConfiguration
@ContextConfiguration(classes = {WebTest.Config.class, WebConfig.class})
public abstract class WebTest extends ServiceBaseTest {

    static final String defaultStartUrl = "http://localhost/start";
    private static final Log log = LogFactory.getLog(WebTest.class);
    protected final ObjectMapper objectMapper = new ObjectMapper();
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QRCodeService qrCodeService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MockPay pay;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SystemService systemService;

    private static <T> Iterable<T> IterableIterator(Iterator<T> iterator) {
        return () -> iterator;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public QRCodeService getQrCodeService() {
        return qrCodeService;
    }

    public MockPay getPay() {
        return pay;
    }

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

    protected MockHttpServletRequestBuilder postWeixin(String urlTemplate, Object... urlVariables) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        builder.header("user-agent", "MicroMessenger");
        return builder;
    }

    protected MockHttpServletRequestBuilder putWeixin(String urlTemplate, Object... urlVariables) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(urlTemplate, urlVariables);
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
     * 断言输入json是一个数组,并且结构上跟inputStream类似
     *
     * @param json
     * @param inputStream
     * @throws IOException
     */
    protected void assertSimilarJsonArray(JsonNode json, InputStream inputStream) throws IOException {
        assertThat(json.isArray())
                .isTrue();
        JsonNode mockArray = objectMapper.readTree(inputStream);
        JsonNode mockOne = mockArray.get(0);

        assertSimilarJsonObject(json.get(0), mockOne);
    }

    /**
     * 断言实际json是类似期望json的
     *
     * @param actual
     * @param excepted
     */
    private void assertSimilarJsonObject(JsonNode actual, JsonNode excepted) {
        assertThat(actual.isObject())
                .isTrue();
        assertThat(actual.fieldNames())
                .containsAll(IterableIterator(excepted.fieldNames()));
    }

    protected ResultMatcher simliarDataJsonAs(String resource) {
        return result -> {
            Resource resource1 = applicationContext.getResource(resource);
            try (InputStream inputStream = resource1.getInputStream()) {
                JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                assertThat(actual.get("total").isNumber())
                        .isTrue();
                JsonNode rows = actual.get("rows");
                assertThat(rows.isArray())
                        .isTrue();
                if (rows.size() == 0) {
                    log.warn("响应的rows为空,无法校验");
                    return;
                }
                JsonNode exceptedAll = objectMapper.readTree(inputStream);
                JsonNode excepted = exceptedAll.get("rows").get(0);

                assertSimilarJsonObject(rows.get(0), excepted);
            }
        };
    }

    /**
     * 磨磨唧唧的建立一个新用户
     *
     * @param invite          邀请者
     * @param withBindingCard 是否自动绑定一个银行卡
     * @return
     * @throws IOException
     */
    protected User helloNewUser(User invite, boolean withBindingCard) throws IOException {
        return helloNewUser(defaultStartUrl, invite, withBindingCard);
    }

    /**
     * 磨磨唧唧的建立一个新用户
     *
     * @param invite          邀请者
     * @param withBindingCard 是否自动绑定一个银行卡
     * @return
     * @throws IOException
     */
    protected User helloNewUser(String startUrl, User invite, boolean withBindingCard) throws IOException {
        driver.get(startUrl);
        String mobile = randomMobile();
        BindingMobilePage page = initPage(BindingMobilePage.class);

        page.submitWithNothing();
        page.inputMobileNumber(mobile);
        page.sendCode();

        if (invite != null)
            if (startUrl.equals(defaultStartUrl)) {
                page.inputInviteCode(invite.getInviteCode());
            } else {
                // 应该看到了邀请者
                page.assertInvite(invite);
            }

        // 找到最近发送的验证码
        page.submitWithCode();

        // 应该到了下一个页面了
        driver.get(defaultStartUrl);

        StartOrderPage startOrderPage = initPage(StartOrderPage.class);

        startOrderPage.assertNoCard();

        SubBranchBank subBranchBank = randomSubBranchBank();

        final String owner = RandomStringUtils.randomAlphanumeric(3);
        final String number = randomBankCard();

        if (withBindingCard) {
            startOrderPage = bindCardOnOrderPage(mobile, startOrderPage, subBranchBank, owner, number);
        }

//        startOrderPage.assertHaveCard();

        // 这里应该是根据已有的支行给出选择

        // 这就对了!
        // 还需要检查 银行是否已设置 地址是否已设置
        String url = currentUserInviteURL();

        // 这个地址需是我们的收款页吧
        assertThat(url)
                .contains("/my");

        //终于找到id了
        Long userId = CashFilter.guideUserFromURL(url, null);

        User user = userRepository.getOne(userId);
        if (withBindingCard) {
            assertThat(user.getCards())
                    .hasSize(1);

            Card card = user.getCards().get(0);
            assertThat(card.getAddress().getCity().getId())
                    .isEqualTo(subBranchBank.getCityCode());
            assertThat(card.getBank())
                    .isEqualTo(subBranchBank.getBank());
            assertThat(card.getSubBranchBank())
                    .isEqualTo(subBranchBank);
            assertThat(card.getOwner())
                    .isEqualTo(owner);
            assertThat(card.getNumber())
                    .isEqualTo(number);
        }


        return user;
    }

    // 1.5 以后更换之前的卡
    public StartOrderPage bindCardOnOrderPage(String mobile, StartOrderPage startOrderPage
            , SubBranchBank subBranchBank, String owner, String number) {
        startOrderPage.toCreateNewOneCard();

        BindingCardPage cardPage = initPage(BindingCardPage.class);
        // 这个用户已经产生
        assertThat(userService.byMobile(mobile))
                .isNotNull();
        //
        // 地址自己选吧


        cardPage.submitWithRandomAddress(subBranchBank, owner, number);
        startOrderPage = initPage(StartOrderPage.class);
        return startOrderPage;
    }

    /**
     * @return 当前登录用户的邀请链接
     * @throws IOException
     */
    protected String currentUserInviteURL() throws IOException {

        // http://app.kuanyes.com/cash/login?code=0218IKrs0MS6Wb1ofros0omPrs08IKrK&state=
        // http://app.kuanyes.com/cash/login?code=0218IKrs0MS6Wb1ofros0omPrs08IKrK&state=
        // /myInviteCode
        driver.get("http://localhost/myInviteCode");
//        MyPage myPage = initPage(MyPage.class);
//        myPage.clickMenu("我的邀请");
//        MyInvitationPage invitePage = initPage(MyInvitationPage.class);
//        invitePage.requestAgent();
        CodePage codePage = initPage(CodePage.class);

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

    public SystemService getSystemService() {
        return systemService;
    }

    @ComponentScan({"me.jiangcai.dating.test"})
    static class Config {

    }

}
