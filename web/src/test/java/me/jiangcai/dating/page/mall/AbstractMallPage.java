package me.jiangcai.dating.page.mall;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author CJ
 */
abstract class AbstractMallPage extends AbstractPage {

    protected WebElement registerLink;
    protected WebElement loginLink;
    AbstractMallPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void assertNotLogin() {
        try {
            registerLink.isDisplayed();
        } catch (NoSuchElementException ex) {
            throw new AssertionError("已登录");
        }
    }

    public void assertLogin() {
        try {
            registerLink.isDisplayed();
            throw new AssertionError("未登录");
        } catch (NoSuchElementException ignored) {
        }
    }

    public RegisterPage openRegisterPage() {
        registerLink.click();
        return initPage(RegisterPage.class);
    }
}
