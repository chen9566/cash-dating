package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManagePasswordPage extends AbstractPage {

    public ManagePasswordPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("密码管理");
    }

    public void submitPassword(String password) {
        webDriver.findElement(By.name("password"))
                .sendKeys(password);
        webDriver.findElement(By.cssSelector("input[type=submit]")).click();
    }
}
