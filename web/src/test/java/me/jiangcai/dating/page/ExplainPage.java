package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author CJ
 */
public class ExplainPage extends AbstractPage {

    private WebElement codeButton;

    public ExplainPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().contains("邀请"))
                .findFirst()
                .ifPresent(element -> codeButton = element);
    }

    public void clickMyCode() {
        codeButton.click();
    }
}
