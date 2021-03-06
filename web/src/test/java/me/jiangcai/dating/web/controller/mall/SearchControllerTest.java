package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.dating.page.ShowOrderPage;
import me.jiangcai.dating.page.mall.FakeGoodsDetailPage;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.LoginPage;
import me.jiangcai.dating.page.mall.SearchPage;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;

import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
public class SearchControllerTest extends AbstractMallTest {

    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;

    protected void createWebDriver() {
        driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
                // DIY by interface.
                .build();
    }

    @Test
    public void buy() throws Exception {
        addRandomFakeGoods();

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

        FakeGoodsDetailPage detailPage = indexPage.openDetailPage(new RandomComparator());
        // 点击购买 我们需要记录它的cookie
        LoginPage loginPage = detailPage.clickBuyWithoutLogin();

        String mobile = registerOnMall(null, loginPage);

        // 当前的页面 应该是详情页面
        detailPage.reloadPageInfo();

        String url = detailPage.clickBuy();
        System.out.println(url);
        // 现在使用另一个driver 扫码支付 这个扫码支付应该是没有被保护
//        ShowOrderPage

        WebDriver wxDriver = createMicroMessengerDriver();
        wxDriver.get(url);
        ShowOrderPage showOrderPage = initPage(ShowOrderPage.class, wxDriver);
        showOrderPage.pay();
        System.out.println(wxDriver.getPageSource());
    }

    @Test
    public void home() throws Exception {
        int count = 200;
        while (count-- > 0)
            addRandomFakeGoods();
        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

//        indexPage.printThisPage();

        FakeGoodsDetailPage detailPage = indexPage.openDetailPage(new RandomComparator());

//        detailPage.printThisPage();

        // 先回来 看看排序这事儿搞得如何
        indexPage = detailPage.backHome();
//        indexPage.printThisPage();

        indexPage.byCheap();
//        indexPage.printThisPage();

        indexPage.byExpensive();
//        indexPage.printThisPage();

        indexPage.byNew();
//        indexPage.printThisPage();

        FakeGoods fakeGoods = randomExitingFakeGoods();
        // 搜索某一个商品
        SearchPage searchPage = indexPage.search(tripName(fakeGoods.getName()));
//        searchPage.printThisPage();
        searchPage.assertHaveGoods(Collections.singleton(fakeGoods));
        // 打开热卖 或者特卖
        searchPage = searchPage.openHotPage();
        searchPage.assertHaveGoods(hotFakeGoods());
        searchPage = searchPage.openSpecialPage();
        searchPage.assertHaveGoods(specialFakeGoods());
        // 打开某一个类目
        indexPage = searchPage.backHome();

        final FakeCategory category = randomEnum(FakeCategory.class);
        searchPage = indexPage.openCategory(category);
        searchPage.assertHaveGoods(categoryFakeGoods(category));
    }

    private List<FakeGoods> categoryFakeGoods(FakeCategory category) {
        return fakeGoodsRepository.findAll((root, query, cb)
                -> cb.and(cb.isTrue(root.get("enable")), cb.equal(root.get("fakeCategory"), category)));
    }

    private String tripName(String input) {
        return input.substring(1, input.length() - 2);
    }

    private FakeGoods randomExitingFakeGoods() {
        return fakeGoodsRepository.findAll((root, query, cb)
                -> cb.isTrue(root.get("enable"))).stream()
                .sorted(new RandomComparator())
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private List<FakeGoods> hotFakeGoods() {
        return fakeGoodsRepository.findAll((root, query, cb)
                -> cb.and(cb.isTrue(root.get("enable")), cb.isTrue(root.get("hot"))));
    }

    private List<FakeGoods> specialFakeGoods() {
        return fakeGoodsRepository.findAll((root, query, cb)
                -> cb.and(cb.isTrue(root.get("enable")), cb.isTrue(root.get("special"))));
    }

}