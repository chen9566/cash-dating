package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * manage/login.html
 * @author CJ
 */
public class PCLoginPage extends AbstractPage {

    private WebElement qrCodeImage;

    public PCLoginPage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("非微信登录");

        qrCodeImage = webDriver.findElement(By.name("qrCode"));

    }

    public PasswordLoginPage password() {
        webDriver.findElement(By.tagName("h2")).findElement(By.tagName("a")).click();
        return initPage(PasswordLoginPage.class);
    }

    public BufferedImage codeImage() throws IOException {
        return toImage(qrCodeImage);
    }
}
