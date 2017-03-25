package me.jiangcai.dating;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.base.Predicate;
import me.jiangcai.chanpay.test.mock.MockPay;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.BindingMobilePage;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.page.sale.MySalePage;
import me.jiangcai.dating.page.sale.SaleIndexPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.WealthService;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.dating.web.WebConfig;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import me.jiangcai.goods.service.ManageGoodsService;
import me.jiangcai.lib.test.page.AbstractPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.WebConnectionHtmlUnitDriver;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected WealthService wealthService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QRCodeService qrCodeService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MockPay pay;
    @Autowired
    private SystemService systemService;
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private ManageGoodsService manageGoodsService;

    private static <T> Iterable<T> IterableIterator(Iterator<T> iterator) {
        return () -> iterator;
    }

    protected MyPage loginAs(User user) {
        return loginAs(user, driver);
    }

    protected MyPage loginAs(User user, WebDriver driver) {
        driver.get("http://localhost/quickLogin/" + user.getId());
        driver.get("http://localhost/my");
        return initPage(MyPage.class, driver);
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
        return initPage(clazz, driver);
    }

    public <T extends AbstractPage> T initPage(Class<T> clazz, WebDriver driver) {
        try {
            T page = PageFactory.initElements(driver, clazz);
//        page.setResourceService(resourceService);
            page.setTestInstance(this);
            page.validatePage();
            return page;
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
    protected void assertSimilarJsonObject(JsonNode actual, JsonNode excepted) {
        assertThat(actual.isObject())
                .isTrue();
        assertThat(actual.fieldNames())
                .containsAll(IterableIterator(excepted.fieldNames()));
    }

    /**
     * @param resource 参考
     * @return 验证这个资源应该是类似的Json Object
     */
    protected ResultMatcher similarJsonAs(String resource) {
        return result -> {
            Resource resource1 = applicationContext.getResource(resource);
            try (InputStream inputStream = resource1.getInputStream()) {
                JsonNode actual = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                assertSimilarJsonObject(actual, objectMapper.readTree(inputStream));
            }
        };
    }

    /**
     * @param resource 参考
     * @return 验证这个资源应该是dataTable风格的json
     */
    protected ResultMatcher similarDataJsonAs(String resource) {
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
     * @param withBindingCard 是否自动绑定一个银行卡,如果供应商不支持分单的话就不会关注这个{@link ArbitrageChannel#useOneOrderForPayAndArbitrage()}
     * @return
     * @throws IOException
     */
    protected User helloNewUser(String startUrl, User invite, boolean withBindingCard) throws IOException {
        String mobile = helloMobile(startUrl, invite);

        // 应该到了下一个页面了

        StartOrderPage startOrderPage = startOrderPage();

        startOrderPage.assertNoCard();

        SubBranchBank subBranchBank = randomSubBranchBank();

        final String owner = RandomStringUtils.randomAlphanumeric(3);
        final String number = randomBankCard();

        ArbitrageChannel channel = systemService.arbitrageChannel(PayMethod.weixin);
        if (withBindingCard && !channel.useOneOrderForPayAndArbitrage()) {
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
        if (withBindingCard && !channel.useOneOrderForPayAndArbitrage()) {
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

    /**
     * 新用户进来
     *
     * @param startUrl
     * @param invite
     * @return
     */
    protected String helloMobile(String startUrl, User invite) {
        if (startUrl == null)
            startUrl = defaultStartUrl;
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
        return mobile;
    }

    // 1.5 以后更换之前的卡
    public StartOrderPage bindCardOnOrderPage(String mobile, StartOrderPage startOrderPage
            , SubBranchBank subBranchBank, String owner, String number) {
        startOrderPage.toCreateNewOneCard();

        bindCard(mobile, subBranchBank, owner, number);
        startOrderPage = initPage(StartOrderPage.class);
        return startOrderPage;
    }

    protected void bindCard(String mobile, SubBranchBank subBranchBank, String owner, String number) {
        BindingCardPage cardPage = initPage(BindingCardPage.class);
        // 这个用户已经产生
        assertThat(userService.byMobile(mobile))
                .isNotNull();
        //
        // 地址自己选吧
        cardPage.submitWithRandomAddress(subBranchBank, owner, number, randomPeopleId());
    }

    /**
     * @return 当前登录用户的邀请链接
     * @throws IOException
     */
    protected String currentUserInviteURL() throws IOException {

        // http://app.kuanyes.com/cash/login?code=0218IKrs0MS6Wb1ofros0omPrs08IKrK&state=
        // http://app.kuanyes.com/cash/login?code=0218IKrs0MS6Wb1ofros0omPrs08IKrK&state=
        // /myInviteCode
//        MyPage myPage = initPage(MyPage.class);
//        myPage.clickMenu("我的邀请");
//        MyInvitationPage invitePage = initPage(MyInvitationPage.class);
//        invitePage.requestAgent();
        CodePage codePage = myPage().toCodePage();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((Predicate<WebDriver>) input -> {
            try {
                codePage.reloadPageInfo();
//                    codePage.getQRCodeImage();
                return true;
            } catch (IllegalArgumentException ex) {
                //
            }
            return false;
        });


        return "http://localhost/my?" + CashFilter.guideUserFromId(Long.valueOf(codePage.getUserId()));
//        try {
//            return qrCodeService.scanImage(codePage.getQRCodeImage());
//        } catch (IllegalArgumentException exception) {
//            codePage.printThisPage();
//            throw exception;
//        }
    }

    public SystemService getSystemService() {
        return systemService;
    }

    protected MyPage myPage() {
        return myPage(driver);
    }

    protected MyPage myPage(WebDriver driver) {
        driver.get("http://localhost/my");
        return initPage(MyPage.class);
    }

    protected StartOrderPage startOrderPage() {
        return startOrderPage(driver);
    }

    protected StartOrderPage startOrderPage(WebDriver driver) {
        driver.get("http://localhost/start");

        try {
            return initPage(StartOrderPage.class, driver);
        } catch (WebDriverException | Error ex) {
            // 在发现问题的时候 可能是因为支付供应商的额外请求
            BindingCardPage bindingCardPage = initPage(BindingCardPage.class, driver);
            SubBranchBank subBranchBank = randomSubBranchBank();

            String owner = RandomStringUtils.randomAlphanumeric(3);
            String number = randomBankCard();
            bindingCardPage.submitWithRandomAddress(subBranchBank, owner, number, randomPeopleId());

            return initPage(StartOrderPage.class, driver);
        }
    }

    protected MockHttpSession mvcLogin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(getWeixin("/start").session(session));
        redirectTo(mockMvc.perform(getWeixin("/login").session(session)), session);
        mockMvc.perform(getWeixin("/start").session(session));
        return session;
    }

    /**
     * 如果遇见302一直执行get
     *
     * @param perform 操作
     * @param session session
     * @return 操作
     * @throws Exception
     */
    protected ResultActions redirectTo(ResultActions perform, MockHttpSession session) throws Exception {
        final MockHttpServletResponse response = perform.andReturn().getResponse();
        if (response.getStatus() == 302) {
            String uri = response.getRedirectedUrl();
            return redirectTo(mockMvc.perform(getWeixin(uri).session(session)), session);
        }
        return perform;
    }

    /**
     * 创建一个项目贷款完整订单
     *
     * @param openId
     * @return
     */
    protected ProjectLoanRequest newProjectLoanRequest(String openId) throws IOException {
        Address address = new Address();
        address.setProvince(PayResourceService.listProvince().stream().max(new RandomComparator()).orElse(null));
        address.setCity(address.getProvince().getCityList().stream().max(new RandomComparator()).orElse(null));

        ProjectLoan projectLoan = new ProjectLoan();
        ProjectLoanRequest loanRequest = wealthService.loanRequest(openId, projectLoan, null
                , new BigDecimal(projectLoan.getMinAmount() + random.nextInt(projectLoan.getAmountInteger() - projectLoan.getMinAmount()))
                , "随意人", org.apache.commons.lang.RandomStringUtils.randomNumeric(18), address, UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), random.nextInt(100), random.nextInt(100), random.nextInt(100), random.nextBoolean());
        wealthService.updateLoanIDImages(loanRequest.getId(), randomImageResourcePath(), randomImageResourcePath(), randomImageResourcePath());
        return loanRequest;
    }

    protected SaleIndexPage saleIndexPage(WebDriver driver) {
        driver.get("http://localhost/sale/index");
        return initPage(SaleIndexPage.class, driver);
    }

    protected SaleIndexPage saleIndexPage() {
        return saleIndexPage(driver);
    }

    protected MySalePage mySalePage() {
        driver.get("http://localhost/sale/my");
        return initPage(MySalePage.class);
    }

    protected CashGoods randomGoodsData() {
        CashGoods goods = new CashGoods() {
            @Override
            public Seller getSeller() {
                return null;
            }

            @Override
            public void setSeller(Seller seller) {

            }

            @Override
            public TradeEntity getOwner() {
                return null;
            }

            @Override
            public void setOwner(TradeEntity owner) {

            }

            @Override
            public boolean isTicketGoods() {
                return false;
            }

            @Override
            protected void moreModel(Map<String, Object> data) {

            }
        };
        goods.setName(UUID.randomUUID().toString());
        goods.setBrand(UUID.randomUUID().toString());
        goods.setDescription(UUID.randomUUID().toString());
        goods.setSubPrice(UUID.randomUUID().toString().substring(0, 25));
        goods.setPrice(randomOrderAmount());
        goods.setRichDetail(UUID.randomUUID().toString());
        goods.setWeight(random.nextInt());
        goods.setHot(random.nextBoolean());
        goods.setFreshly(random.nextBoolean());
        goods.setSpecial(random.nextBoolean());

        return goods;
    }

    protected void addRandomFakeGoods() throws IOException {
        RootAuthentication.runAsRoot(() -> {
            FakeGoods goods;
            try {
                goods = mallGoodsService.addFakeGoods(UUID.randomUUID().toString(), randomOrderAmount().toString());
            } catch (IOException e) {
                throw new InternalError(e);
            }
            // 设定其属性
            CashGoods cashGoods = randomGoodsData();

            goods.setFakeCategory(FakeCategory.values()[random.nextInt(FakeCategory.values().length)]);
            goods.setSales(random.nextInt(100) + 1);
            goods.setStock(random.nextInt(100) + 1);
            goods.setDiscount("7.1");

            goods.setSubPrice(cashGoods.getSubPrice());
            goods.setRichDetail(cashGoods.getRichDetail());
            goods.setPrice(cashGoods.getPrice());
            goods.setBrand(cashGoods.getBrand());
            goods.setDescription(cashGoods.getDescription());
            goods.setName(cashGoods.getName());
            goods.setWeight(cashGoods.getWeight());
            goods.setHot(cashGoods.isHot());
            goods.setFreshly(cashGoods.isFreshly());
            goods.setSpecial(cashGoods.isSpecial());

            mallGoodsService.saveGoods(goods);
            manageGoodsService.enableGoods(goods, mallGoodsService::saveGoods);
        });

    }

    @ComponentScan({"me.jiangcai.dating.test"})
    static class Config {

    }

}
