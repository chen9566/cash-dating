package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 卡券类商品详情页面
 * carddetails.html
 *
 * @author CJ
 */
public class TicketGoodsDetailPage extends AbstractPage {

    private WebElement readyToBuy;
    private WebElement orderCreator;

    public TicketGoodsDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
//        printThisPage();
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("商品详情");
    }

    public TicketPayPage buy(int count) {
        readyToBuy.click();
        assertThat(orderCreator.isDisplayed())
                .isTrue();
        orderCreator.click();
        try {
            String text = webDriver.switchTo().alert().getText();
            throw new AssertionError(text);
        } catch (NoAlertPresentException ignored) {
        }
        return initPage(TicketPayPage.class);
    }
}
