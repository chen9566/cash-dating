package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 二维码界面
 * <p>
 * 可以分享这个二维码也可以直接支付
 *
 * @author CJ
 */
public class QRCodePage extends AbstractPage {

    private WebElement amountSpan;
    private WebElement image;
    private WebElement shareButton;

    public QRCodePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.className("nun")).stream()
                .filter(element -> element.isDisplayed())
                .findAny()
                .ifPresent(element -> amountSpan = element);

        webDriver.findElements(By.tagName("img")).stream()
                .filter(element -> "qrCode".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> image = element);

        shareButton = webDriver.findElement(By.tagName("button"));

        assertThat(amountSpan)
                .isNotNull();
        assertThat(image)
                .isNotNull();
        assertThat(shareButton)
                .isNotNull();
    }

    private static final Field elementField;

    static {
        try {
            elementField = HtmlUnitWebElement.class.getDeclaredField("element");
            elementField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InternalError("炸!,版本更新了?",e);
        }
    }

    public BufferedImage scanCode() throws IOException {
        try {
            HtmlImage image = (HtmlImage) elementField.get(this.image);
            return image.getImageReader().read(0);
        } catch (IllegalAccessException e) {
            throw new InternalError("炸!,版本更新了?",e);
        }
    }
}
