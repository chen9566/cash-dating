package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * index.html
 *
 * @author CJ
 */
public class IndexPage extends GoodsListPage {

    public IndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("款爷商城");
    }

    public void byCheap() {
        by("cheap");
    }

    public void byExpensive() {
        by("expensive");
    }

    public void byNew() {
        by("new");
    }

    private void by(String name) {
        webDriver.findElements(By.className("orderAble")).stream()
                .filter(element -> element.getAttribute("data-value").equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到按照" + name + "排序的按钮"))
                .click();
        reloadPageInfo();
    }
}
