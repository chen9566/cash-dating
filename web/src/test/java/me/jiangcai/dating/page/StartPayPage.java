package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 开始收款页面
 *
 * @author CJ
 */
public class StartPayPage extends AbstractPage {

    private WebElement amountInput;
    private WebElement commentInput;
    private WebElement button;

    public StartPayPage(WebDriver webDriver) {
        super(webDriver);
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

        webDriver.findElements(By.tagName("button")).stream()
                .filter(element -> "submit".equals(element.getAttribute("type")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> button = element);

        assertThat(amountInput)
                .isNotNull();
        assertThat(commentInput)
                .isNotNull();
        assertThat(button)
                .isNotNull();
    }

    public void pay(int amount, String comment) {
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        commentInput.clear();
        commentInput.sendKeys(comment);
        button.click();
    }
}
