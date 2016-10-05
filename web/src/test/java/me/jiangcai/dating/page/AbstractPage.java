package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author CJ
 */
public abstract class AbstractPage extends me.jiangcai.lib.test.page.AbstractPage {

    private static final Field elementField;

    static {
        try {
            elementField = HtmlUnitWebElement.class.getDeclaredField("element");
            elementField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
    }

    public AbstractPage(WebDriver webDriver) {
        super(webDriver);
    }

    protected void assertAlert(String text) {
    }

    /**
     * 你懂的
     * @param element
     * @return
     * @throws IOException
     */
    protected BufferedImage toImage(WebElement element) throws IOException {
        try {
            HtmlImage image = (HtmlImage) elementField.get(element);
            return image.getImageReader().read(0);
        } catch (IllegalAccessException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
    }
}
