package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款身份证上传页面
 * id.html
 *
 * @author CJ
 */
public class LoanIDPage extends AbstractPage {
    public LoanIDPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("上传身份证照片");
    }

    public LoanHandIDPage next(String backId, String frontId) {
        setElementAttributeValue(webDriver.findElement(By.cssSelector("#fm img")), "path", backId);
        setElementAttributeValue(webDriver.findElement(By.cssSelector("#zm img")), "path", frontId);
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("下一步"))
                .findFirst()
                .ifPresent(WebElement::click);
        return initPage(LoanHandIDPage.class);
    }

}
