package me.jiangcai.dating.page.sale;

import com.google.common.base.Predicate;
import me.jiangcai.dating.page.AbstractPage;
import me.jiangcai.dating.page.ShowOrderPage;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 准备支付页面
 * cardplay.html
 *
 * @author CJ
 */
public class TicketPayPage extends AbstractPage {

    private WebElement payButton;

    public TicketPayPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("确认订单");
    }

    /**
     * 完成支付
     *
     * @param obj
     */
    public TicketPaySuccessPage toPay(Object obj) throws Exception {
        payButton.click();
        try {
            new WebDriverWait(webDriver, 5).until((Predicate<WebDriver>)
                    input -> input != null && input.getTitle().equalsIgnoreCase("收款二维码"));
        } catch (TimeoutException exception) {
            payButton.click();
            try {
                new WebDriverWait(webDriver, 5).until((Predicate<WebDriver>)
                        input -> input != null && input.getTitle().equalsIgnoreCase("收款二维码"));
            } catch (TimeoutException e) {
                printThisPage();
                throw e;
            }
        }

        initPage(ShowOrderPage.class).pay();
        return initPage(TicketPaySuccessPage.class);
    }
}
