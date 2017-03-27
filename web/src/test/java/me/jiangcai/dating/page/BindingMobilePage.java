package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.User;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * register.html
 * @author CJ
 */
public class BindingMobilePage extends AbstractPage {

    private WebElement mobileInput;
    private WebElement codeInput;
    private WebElement registerButton;
    @FindBy(id = "btn-mask")
    private WebElement buttonSend;
    private WebElement inviteCodeInput;

    public BindingMobilePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        // 有可输入的手机号码
        assertTitle("注册");

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> element.isDisplayed() && element.getAttribute("placeholder").contains("手机号码"))
                .findAny().ifPresent(element -> mobileInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> element.isDisplayed() && element.getAttribute("placeholder").contains("验证码"))
                .findAny().ifPresent(element -> codeInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> element.isDisplayed() && element.getAttribute("placeholder").contains("邀请码"))
                .findAny().ifPresent(element -> inviteCodeInput = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(element -> element.getText().equals("注册") && element.isDisplayed())
                .findAny().ifPresent(element -> registerButton = element);

//        webDriver.findElements(By.className("yzm")).stream()
//                .filter(element -> element.isDisplayed() && element.getText().equals("获取验证码"))
//                .findAny().ifPresent(element -> buttonSend = element);

        assertThat(mobileInput).isNotNull();
        assertThat(mobileInput.isDisplayed()).isTrue();
        assertThat(codeInput).isNotNull();
        assertThat(registerButton).isNotNull();
        assertThat(buttonSend).isNotNull();
    }

    public void submitWithNothing() {
//        registerButton.click();
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
        registerButton.click();
//        System.out.println(webDriver.getPageSource());
    }

    /**
     * 录入邀请码
     *
     * @param code
     */
    public void inputInviteCode(String code) {
        assertThat(inviteCodeInput.isDisplayed())
                .isTrue();
        inviteCodeInput.clear();
        inviteCodeInput.sendKeys(code);
    }

    /**
     * 这个用户是被user邀请而来的
     *
     * @param user 邀请者
     */
    public void assertInvite(User user) {
//        printThisPage();
        assertThat(inviteCodeInput.getAttribute("value"))
                .isEqualTo(user.getInviteCode());
        // 总有一个span 是看到了user的
        assertThat(webDriver.findElements(By.tagName("span")).stream()
                .filter(WebElement::isDisplayed)
                .anyMatch(webElement -> webElement.getText().contains(user.getNickname())))
                .isTrue();
    }
}
