package me.jiangcai.dating.page;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ProjectSuccessVerifyPage extends AbstractPage {

    @FindBy(id = "btn-mask")
    private WebElement buttonToSend;
    private WebElement verificationCode;
    private WebElement buttonToSubmit;
    private WebElement myAlert;

    public ProjectSuccessVerifyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("验证手机号码");
    }

    public void clickSendCode() {
        buttonToSend.click();
        try {
            String text = webDriver.switchTo().alert().getText();
            throw new AssertionError("看到了：" + text);
        } catch (NoAlertPresentException ignored) {
        }
    }

    public void inputCode(String code) {
        verificationCode.clear();
        verificationCode.sendKeys(code);
        buttonToSubmit.click();
    }

    public void assertMessageExisting() {
        assertThat(myAlert.isDisplayed())
                .isTrue();
    }
}
