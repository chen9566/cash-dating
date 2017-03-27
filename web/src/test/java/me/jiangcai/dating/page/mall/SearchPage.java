package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class SearchPage extends GoodsListPage {

    public SearchPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("款爷商城");
        assertThat(webDriver.findElement(By.tagName("body")).getAttribute("data-search-result"))
                .isNotEmpty();
    }
}
