package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.dating.page.mall.FakeGoodsDetailPage;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.SearchPage;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
public class SearchControllerTest extends WebTest {
    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;

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