package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class BindingMobilePage extends AbstractPage {

    private WebElement mobileInput;
    private WebElement codeInput;
    private WebElement button;
    private WebElement buttonSend;

    public BindingMobilePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        // 有可输入的手机号码
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("注册");

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> element.isDisplayed() && element.getAttribute("placeholder").contains("手机号码"))
                .findAny().ifPresent(element -> mobileInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> element.isDisplayed() && element.getAttribute("placeholder").contains("验证码"))
                .findAny().ifPresent(element -> codeInput = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(element -> element.getText().equals("确定") && element.isDisplayed())
                .findAny().ifPresent(element -> button = element);

        webDriver.findElements(By.className("yzm")).stream()
                .filter(element -> element.isDisplayed() && element.getText().equals("获取验证码"))
                .findAny().ifPresent(element -> buttonSend = element);

        assertThat(mobileInput).isNotNull();
        assertThat(codeInput).isNotNull();
        assertThat(button).isNotNull();
        assertThat(buttonSend).isNotNull();
    }

    public void submitWithNothing() {
//        button.click();
        assertAlert("手机号码");

    }

    public void inputMobileNumber(String text) {
        mobileInput.clear();
        mobileInput.sendKeys(text);
    }

    /**
     * 发送验证码
     */
    public void sendCode() {
        buttonSend.click();
        try {
            webDriver.switchTo().alert();
            throw new AssertionError("应该看不到错误的");
        } catch (NoAlertPresentException ignored) {
        }

    }

    public void submitWithCode() {
        codeInput.clear();
        codeInput.sendKeys("1234");
        button.click();
    }
}
