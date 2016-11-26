package me.jiangcai.dating.page;

import me.jiangcai.dating.util.Common;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 提现页
 * now.html
 *
 * @author CJ
 */
public class WithdrawPage extends AbstractPage {
    // 可用余额
    private WebElement balance;
    @FindBy(id = "_content")
    private WebElement input;
    @FindBy(id = "_btn1")
    private WebElement button;

    public WithdrawPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("提现");
    }

    public void assertBalance(BigDecimal balance) {
        assertThat(this.balance.getText())
                .isEqualTo(Common.CurrencyFormat(balance));
    }

    public WithdrawResultPage withdraw(BigDecimal number) {
        input.clear();
        input.sendKeys(number.toString());
        button.click();
        return initPage(WithdrawResultPage.class);
    }
}
