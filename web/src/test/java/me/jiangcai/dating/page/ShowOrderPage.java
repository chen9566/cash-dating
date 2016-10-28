package me.jiangcai.dating.page;

import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;

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

    public String orderId() {
        String url = webDriver.getCurrentUrl();
        String id = url.substring(url.lastIndexOf("/") + 1);
        if (id.contains("?")) {
            int index = id.indexOf("?");
            return id.substring(0, index);
        }
        return id;
    }


    public BufferedImage scanCode() throws IOException {
        return toImage(image);
    }

    public void assertAmount(double amount) {
        assertThat(amountSpan.getText())
                .isEqualTo(Common.CurrencyFormat(amount));
    }

    /**
     * 支付这个订单
     */
    public void pay() throws Exception {
        mockWeixinPay(orderId(), scanCode());
    }

}
