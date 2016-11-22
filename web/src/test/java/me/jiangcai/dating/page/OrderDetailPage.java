package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class OrderDetailPage extends AbstractPage {

    private WebElement button;

    public OrderDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("订单详情");

        button = webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains("重试"))
                .findFirst()
                .orElse(null);
    }

    public void assertSuccess(CashOrder order) {
        commonAssert(order);
        // 银行
        fieldCheck(webElement -> webElement.getText().contains("尾号"));
        fieldCheck(webElement -> webElement.getText().equals(Common.CurrencyFormat(order.getWithdrawalAmount())));
        // 找不到重试按钮
        assertThat(button)
                .isNull();
    }

    public void assertTransferring(CashOrder order) {
        commonAssert(order);
        assertThat(button)
                .isNull();
    }

    public void assertFailed(CashOrder order) {
        commonAssert(order);
        // 银行
        // 找不到重试按钮
        assertThat(button)
                .isNotNull();
        assertThat(button.isDisplayed())
                .isTrue();
    }

    private void commonAssert(CashOrder order) {
        fieldCheck(webElement -> webElement.getText().equals(order.getFriendlyId()));
        fieldCheck(webElement -> webElement.getText().equals(Common.CurrencyFormat(order.getAmount())));
    }

    private void fieldCheck(Predicate<WebElement> webElementPredicate) {
        assertThat(webDriver.findElements(By.className("pull-right")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElementPredicate)
                .count()).isGreaterThan(0);
    }

    public void retry() {
        button.click();
    }
}
