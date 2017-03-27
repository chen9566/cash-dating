package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class FakeGoodsDetailPage extends AbstractMallPage {
    public FakeGoodsDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        printThisPage();
        assertThat(webDriver.findElement(By.tagName("body")).getAttribute("data-id"))
                .isNotEmpty();
    }
}
