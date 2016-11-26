package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import java.lang.reflect.Field;

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

    public BindingCardPage next(String backId, String frontId) {
        setElementAttributeValue(webDriver.findElement(By.cssSelector("#fm img")), "path", backId);
        setElementAttributeValue(webDriver.findElement(By.cssSelector("#zm img")), "path", frontId);
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("下一步"))
                .findFirst()
                .ifPresent(WebElement::click);
        return initPage(BindingCardPage.class);
    }

    private void setElementAttributeValue(WebElement element, String attributeName, String value) {
        try {
            Field field = HtmlUnitWebElement.class.getDeclaredField("element");
            field.setAccessible(true);
            HtmlElement htmlHiddenInput = (HtmlElement) field.get(element);
            htmlHiddenInput.setAttribute(attributeName, value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
