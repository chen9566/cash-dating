package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 上传手持身份证的照片
 * handid.html
 *
 * @author CJ
 */
public class LoanHandIDPage extends AbstractPage {

    public LoanHandIDPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("上传身份证照片");
    }

    public LoanCompletedPage next(String resourcePath) {
        setElementAttributeValue(webDriver.findElement(By.cssSelector("#hand img")), "path", resourcePath);
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("下一步"))
                .findFirst()
                .ifPresent(WebElement::click);
        return initPage(LoanCompletedPage.class);
    }
}
