package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MySalePage extends AbstractPage {
    public MySalePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("我的");
    }
}
