package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 订单查询结果页面
 *
 * @author CJ
 */
public class ManageOrderResultPage extends AbstractPage {

    public ManageOrderResultPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("订单列表");
    }

    public void platformCheckAll() {
        webDriver.findElements(By.name("platformCheck")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains("平台审核所有订单"))
                .findFirst()
                .ifPresent(WebElement::click);

        sureToGo();
    }

    private void sureToGo() {
        new WebDriverWait(webDriver, 3)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(@Nullable WebDriver input) {
                        return input != null && input.findElement(By.id("dialog-confirm")).isDisplayed();
                    }
                });

        webDriver.findElements(By.cssSelector("button.ui-button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains("确定"))
                .peek(System.out::println)
                .findFirst()
                .ifPresent(WebElement::click);
    }

    public ManageOrderResultPage searchAgain(String search) {
        webDriver.navigate().back();
        initPage(ManageOrderPage.class).search(search);
        return initPage(ManageOrderResultPage.class);
    }

    public void platFormCheckOrder(String orderId) {
        webDriver.findElement(By.id(orderId))
                .findElements(By.name("platformCheck")).stream()
                .filter(WebElement::isDisplayed)
//                .filter(webElement -> webElement.getText().contains("平台审核所有订单"))
                .findFirst()
                .ifPresent(WebElement::click);
        sureToGo();
    }
}
