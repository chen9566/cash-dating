package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Predicate;

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


    public void inputSelect(WebElement formElement, String inputName, String label) {
        Predicate<String> predicate = label::equals;

        inputSelect(formElement, inputName, predicate);
    }

    public void inputSelect(WebElement formElement, String inputName, Predicate<String> predicate) {
        WebElement input = formElement.findElement(By.name(inputName));

        if (input.getAttribute("class") != null && input.getAttribute("class").contains("chosen-select")) {
            // 换一个方式
            WebElement container = formElement.findElements(By.className("chosen-container"))
                    .stream()
                    .filter(webElement -> webElement.getAttribute("title") != null && webElement.getAttribute("title")
                            .equals(input.getAttribute("title")))
                    .findAny().orElseThrow(() -> new IllegalStateException("使用了chosen-select,但没看到chosen-container"));

            container.click();
            for (WebElement element : container.findElements(By.cssSelector("li.active-result"))) {
                if (predicate.test(element.getText())) {
                    element.click();
                    return;
                }
            }
            return;
        }
        //chosen-container chosen-container-single and same title
        // li.active-result

        input.clear();
        for (WebElement element : input.findElements(By.tagName("option"))) {
//            System.out.println(element.getText());
            if (predicate.test(element.getText())) {
                element.click();
                return;
            }
        }
    }
}
