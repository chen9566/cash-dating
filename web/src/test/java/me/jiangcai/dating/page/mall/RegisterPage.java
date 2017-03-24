package me.jiangcai.dating.page.mall;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author CJ
 */
public class RegisterPage extends AbstractMallPage {

    private WebElement mobileInput;
    private WebElement codeInput;
    private WebElement registerButton;
    @FindBy(id = "btn-mask")
    private WebElement buttonSend;
    private WebElement inviteCodeInput;

    RegisterPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("用户注册");
    }

    public void registerAsRandom() {

    }
}
