package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.thymeleaf.util.NumberPointType;
import org.thymeleaf.util.NumberUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 二维码界面,展示订单界面
 * 可以分享这个二维码也可以直接支付
 *
 * @author CJ
 */
public class ShowOrderPage extends AbstractPage {

    private WebElement amountSpan;
    private WebElement image;
    private WebElement shareButton;

    public ShowOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.className("nun")).stream()
                .filter(WebElement::isDisplayed)
                .findAny()
                .ifPresent(element -> amountSpan = element);

        webDriver.findElements(By.tagName("img")).stream()
                .filter(element -> "qrCode".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> image = element);

        shareButton = webDriver.findElement(By.tagName("button"));

        assertThat(amountSpan)
                .isNotNull();
        assertThat(amountSpan.isDisplayed()).isTrue();
        assertThat(image)
                .isNotNull();
        assertThat(shareButton)
                .isNotNull();
    }


    public BufferedImage scanCode() throws IOException {
        return toImage(image);
    }

    public void assertAmount(double amount) {
        assertThat(amountSpan.getText())
                .isEqualTo(NumberUtils.format(amount, 1, NumberPointType.COMMA, 2, NumberPointType.POINT, Locale.CHINA));
    }
}
