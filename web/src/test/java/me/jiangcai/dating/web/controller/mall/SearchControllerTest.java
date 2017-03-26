package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.mall.FakeGoodsDetailPage;
import me.jiangcai.dating.page.mall.IndexPage;
import org.junit.Test;

/**
 * @author CJ
 */
public class SearchControllerTest extends WebTest {
    @Test
    public void home() throws Exception {
        int count = 200;
        while (count-- > 0)
            addRandomFakeGoods();
        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

        indexPage.printThisPage();

        FakeGoodsDetailPage detailPage = indexPage.openDetailPage(new RandomComparator());

        detailPage.printThisPage();
    }

}