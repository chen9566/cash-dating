package me.jiangcai.dating.page.mall;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author CJ
 */
public class RegisterPage extends AbstractMallPage {

    private WebElement mobile;
    private WebElement verificationCode;
    @FindBy(id = "btn-mask")
    private WebElement buttonSend;

    private WebElement password;
    private WebElement confirm_password;
    @FindBy(css = "[input=submit]")
    private WebElement submitButton;

    RegisterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("用户注册");
    }

    public void registerAsRandom(String mobile, String password) {
        this.mobile.clear();
        this.mobile.sendKeys(mobile);

        // 验证码
        buttonSend.click();
        try {
            webDriver.switchTo().alert();
            throw new AssertionError("应该看不到错误的");
        } catch (NoAlertPresentException ignored) {
        }

        verificationCode.clear();
        verificationCode.sendKeys("2334");

        this.password.clear();
        this.password.sendKeys(password);
        this.confirm_password.clear();
        this.confirm_password.sendKeys(password);

        submitButton.click();
    }
}
