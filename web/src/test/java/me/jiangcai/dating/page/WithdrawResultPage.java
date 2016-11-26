package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class WithdrawResultPage extends AbstractPage {
    public WithdrawResultPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("收款状态");
    }

    public WithdrawListPage back() {
        webDriver.findElement(By.tagName("button")).click();
        return initPage(WithdrawListPage.class);
    }
}
