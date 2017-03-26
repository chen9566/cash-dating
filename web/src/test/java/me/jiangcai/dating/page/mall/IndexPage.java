package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Comparator;

/**
 * index.html
 *
 * @author CJ
 */
public class IndexPage extends AbstractMallPage {

    public IndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("款爷商城");
    }

    public FakeGoodsDetailPage openDetailPage(Comparator<Object> comparator) {
        webDriver.findElements(By.className("clickHref")).stream()
                .sorted(comparator)
                .findFirst().orElseThrow(() -> new IllegalStateException("什么都没找到"))
                .click();
        return initPage(FakeGoodsDetailPage.class);
    }
}
