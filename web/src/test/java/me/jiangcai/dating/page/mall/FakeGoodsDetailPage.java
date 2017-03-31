package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class FakeGoodsDetailPage extends AbstractMallPage {
    @FindBy(css = ".buy a")
    private WebElement buyButton;

    public FakeGoodsDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
//        printThisPage();
        assertThat(webDriver.findElement(By.tagName("body")).getAttribute("data-id"))
                .isNotEmpty();
    }

    public LoginPage clickBuyWithoutLogin() {
        buyButton.click();
        return initPage(LoginPage.class);
    }

    public String clickBuy() throws InterruptedException, IOException {
        buyButton.click();

        Thread.sleep(500);

        WebElement image = webDriver.findElements(By.name("payImage")).get(0);
        return getQRCodeService().scanImage(toImage(image));
    }
}
