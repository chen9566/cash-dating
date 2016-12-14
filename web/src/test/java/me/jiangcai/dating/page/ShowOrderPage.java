package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

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

        getQRImage(webDriver)
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

    private Optional<WebElement> getQRImage(WebDriver webDriver) {
        return webDriver.findElements(By.tagName("img")).stream()
                .filter(element -> "qrCode".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny();
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


    private BufferedImage scanCode(PayChannel channel) throws IOException {
        WebElement ele = webDriver.findElements(By.className("payChannel")).stream()
                .filter(webElement -> webElement.getAttribute("data-id").equals(channel.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到渠道" + channel));
        assertThat(ele.isDisplayed())
                .isTrue();
        ele.click();
        // 等待知道图片出来
        new WebDriverWait(webDriver, 2).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                if (input == null)
                    return false;
                return getQRImage(input).orElse(null).getAttribute("src") != null;
            }
        });
        return toImage(getQRImage(webDriver).orElse(null));
    }

    public void assertAmount(double amount) {
        assertThat(amountSpan.getText())
                .isEqualTo(Common.CurrencyFormat(amount));
    }

    /**
     * 支付这个订单
     *
     * @param channel
     */
    public void pay(PayChannel channel) throws Exception {
        mockWeixinPay(orderId(), scanCode(channel));
    }

}
