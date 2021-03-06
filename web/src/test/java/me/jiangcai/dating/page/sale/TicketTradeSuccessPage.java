package me.jiangcai.dating.page.sale;

import com.google.common.base.Predicate;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * cardplayfinish.html 订单详情
 *
 * @author CJ
 */
public class TicketTradeSuccessPage extends AbstractPage {
    public TicketTradeSuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    public static void clickConfirm(WebDriver webDriver) {
        new WebDriverWait(webDriver, 5)
                .until((Predicate<WebDriver>) input -> input != null
                        && input.findElement(By.className("Use")).isDisplayed());

        webDriver.findElement(By.className("confirm")).click();
    }

    public static WebElement findTicketCode(WebDriver webDriver) {
        new WebDriverWait(webDriver, 5)
                .until((Predicate<WebDriver>) input -> input != null
                        && anyTicketCode(input) != null);

        return anyTicketCode(webDriver);
    }

    private static WebElement anyTicketCode(WebDriver driver) {
        return driver.findElements(By.className("ticketCode")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst().orElse(null);
    }

    @Override
    public void validatePage() {
        assertTitle("订单详情");
    }

    /**
     * @return 随便打开一个，然后把二维码结果返回
     */
    public String useRandomOne() throws IOException {
        WebElement toUsePanel = webDriver.findElement(By.className("Use"));
        assertThat(toUsePanel.isDisplayed())
                .isFalse();
        WebElement link = webDriver.findElements(By.className("usered")).stream()
                .filter(WebElement::isDisplayed)
                .max(new ServiceBaseTest.RandomComparator()).orElse(null);
        link.click();

        clickConfirm(webDriver);

        // 肯定会有一个ticketCode展示出来的
        WebElement code = findTicketCode(webDriver);

        WebElement image = code.findElement(By.name("qrCode"));
        return getQRCodeService().scanImage(toImage(image));
    }

    /**
     * @param code 这个电子券应该已经 已用
     */
    public void assertUsed(TicketCode code) {
        WebElement link = webDriver.findElements(By.className("usered")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getAttribute("data-id").equals(code.getTicketCodePK().getURIFormat()))
                .findAny().orElseThrow(() -> new IllegalStateException("找不到" + code));

        assertThat(link.getAttribute("class").contains("useclose"));
    }
}
