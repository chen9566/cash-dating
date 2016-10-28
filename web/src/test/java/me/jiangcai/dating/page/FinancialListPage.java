package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class FinancialListPage extends AbstractPage {
    public FinancialListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("资金明细");
        printThisPage();
    }
}
