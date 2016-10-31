package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class LogoutPage extends AbstractPage {

    public LogoutPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("回头见");
//        printThisPage();
    }

    public void assertContain(String name) {
        assertThat(webDriver.findElements(By.tagName("p")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(webElement -> webElement.getText().contains(name)))
                .isTrue();
    }

    public void loginAgain() {
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains("回去"))
                .findFirst().ifPresent(WebElement::click);
    }
}
