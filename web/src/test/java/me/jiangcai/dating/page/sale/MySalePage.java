package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

    /**
     * 可用的电子券
     *
     * @param count
     */
    public void assertUsableTicket(int count) {
        assertThat(webDriver.findElement(By.className("nouse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }

    /**
     * 已用过的电子券
     *
     * @param count
     */
    public void assertUsedTicket(int count) {
        assertThat(webDriver.findElement(By.className("douse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }

    public void clickUsableOneAndUseIt() {
        webDriver.findElement(By.className("nouse")).findElements(By.tagName("ul")).stream()
                .max(new ServiceBaseTest.RandomComparator())
                .orElse(null)
                .click();

        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("我的优惠券");
        // 点击 button 立即使用
        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("立即使用"))
                .findFirst().orElseThrow(() -> new IllegalStateException("找不到使用按钮"))
                .click();

        TicketTradeSuccessPage.clickConfirm(webDriver);
        TicketTradeSuccessPage.findTicketCode(webDriver);

        webDriver.navigate().back();
    }

    public void clickUsedOneAndBack() {
        // 点击 已使用
        webDriver.findElement(By.className("n")).click();

        //
        webDriver.findElement(By.className("douse")).findElements(By.tagName("ul")).stream()
                .max(new ServiceBaseTest.RandomComparator())
                .orElse(null)
                .click();

        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("我的优惠券");

        webDriver.navigate().back();
    }
}
