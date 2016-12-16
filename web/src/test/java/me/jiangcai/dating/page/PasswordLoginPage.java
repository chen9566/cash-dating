package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class PasswordLoginPage extends AbstractPage {

    private WebElement username;
    private WebElement password;

    public PasswordLoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("手机密码登录");
    }


    public void submit(String mobileNumber, String password) {
        username.clear();
        username.sendKeys(mobileNumber);
        this.password.clear();
        this.password.sendKeys(password);
        webDriver.findElement(By.cssSelector("input[type=submit]")).click();
    }
}
