package me.jiangcai.dating.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.math.RoundingMode;
import java.text.NumberFormat;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 开始收款页面
 *
 * @author CJ
 */
public class StartOrderPage extends AbstractPage {

    private static final Log log = LogFactory.getLog(StartOrderPage.class);

    private WebElement amountInput;
    private WebElement commentInput;
    @FindBy(id = "_btn1")
    private WebElement button;

    public StartOrderPage(WebDriver webDriver) {
        super(webDriver);
        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> "amount".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> amountInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> "comment".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> commentInput = element);

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(element -> "submit".equals(element.getAttribute("type")) && element.isDisplayed())
//                .findAny()
//                .ifPresent(element -> button = element);

        assertThat(amountInput)
                .isNotNull();
        assertThat(amountInput.isDisplayed()).isTrue();
        assertThat(commentInput)
                .isNotNull();
        assertThat(button)
                .isNotNull();
        assertThat(button.isDisplayed())
                .isTrue();
    }

    public void pay(double amount, String comment) {
        amountInput.clear();

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setRoundingMode(RoundingMode.HALF_UP);//设置四舍五入
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(false);

        log.debug(amount);
        log.debug(format.format(amount));
        amountInput.sendKeys(format.format(amount));
//        commentInput.clear();
//        commentInput.sendKeys(comment);
        button.click();
    }

    public void assertNoCard() {
        assertThat(webDriver.findElements(By.className("nocard")))
                .isNotEmpty();
    }

    public void assertHaveCard() {
        assertThat(webDriver.findElements(By.className("card")))
                .isNotEmpty();
    }

    /**
     * 去建立唯一的一个卡
     */
    public void toCreateNewOneCard() {
        WebElement host;
        if (webDriver.findElements(By.className("nocard")).isEmpty()) {
            host = webDriver.findElement(By.className("card"));
        } else
            host = webDriver.findElement(By.className("nocard"));

        host.findElement(By.tagName("a")).click();
    }
}
