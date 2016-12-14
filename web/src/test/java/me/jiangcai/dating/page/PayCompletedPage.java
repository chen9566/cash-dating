package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 支付完成以后的页面,可能是其他人看到的,也可能是用户看自己的
 *
 * @author CJ
 */
public class PayCompletedPage extends AbstractPage {
    public PayCompletedPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("付款成功");
    }

    /**
     * 断言 这个订单尚未提现但刚刚完成支付
     */
    public void assertJustPayWithoutWithdrawal() {
//        printThisPage();
        webDriver.findElements(By.className("pay-done-1")).stream()
                .filter(WebElement::isDisplayed)
                .findAny()
                .orElseThrow(AssertionError::new);
    }

    /**
     * 断言这是来自「我」访问自己的订单
     */
    public void assertVisitByMyself() {
        // 找到 返回首页 的button
        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> "返回首页".equals(webElement.getText()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }
}
