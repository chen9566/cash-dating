package me.jiangcai.dating.page.mall;

import me.jiangcai.dating.page.AbstractPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author CJ
 */
abstract class AbstractMallPage extends AbstractPage {

    private static final Log log = LogFactory.getLog(AbstractMallPage.class);
    protected WebElement registerLink;
    protected WebElement loginLink;
    protected WebElement welcomeSpan;

    AbstractMallPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void assertNotLogin() {
        try {
            registerLink.isDisplayed();
        } catch (NoSuchElementException ex) {
            printThisPage();
            throw new AssertionError("已登录");
        }
    }

    public void assertLogin() {
        try {
            registerLink.isDisplayed();
            printThisPage();
            throw new AssertionError("未登录");
        } catch (NoSuchElementException ignored) {
            log.info(welcomeSpan.getText());
        }
    }

    public RegisterPage openRegisterPage() {
//        System.out.println(registerLink);
        registerLink.click();
        return initPage(RegisterPage.class);
    }

    public LoginPage openLoginPage() {
        loginLink.click();
        return initPage(LoginPage.class);
    }
}
