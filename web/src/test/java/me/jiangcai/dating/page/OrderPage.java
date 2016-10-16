package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class OrderPage extends AbstractPage {
    public OrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        System.out.println(webDriver.getPageSource());
        assertThat(webDriver.getTitle())
                .isEqualTo("资金流水");
    }
}
