package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 展示付款码
 *
 * @author CJ
 */
public class PayToMePage extends AbstractPage {
    public PayToMePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("付款码");

    }

    public void changeTitle(String comment) {
        new Actions(webDriver)
                .doubleClick(webDriver.findElement(By.tagName("h1")))
                .build()
                .perform();

        new WebDriverWait(webDriver, 10)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(@Nullable WebDriver input) {
                        if (input == null)
                            return false;
                        return input.findElement(By.cssSelector("input[name=name]")).isDisplayed();
                    }
                });

        webDriver.findElement(By.cssSelector("input[name=name]")).sendKeys(comment);
        webDriver.findElement(By.className("nameSubmit")).click();

        //再拿图
        assertThat(webDriver.findElement(By.tagName("h1")).getText())
                .isEqualTo(comment);
    }

    public BufferedImage checkOutImage() throws IOException {
        return toImage(webDriver.findElement(By.name("qrCode")));
    }
}
