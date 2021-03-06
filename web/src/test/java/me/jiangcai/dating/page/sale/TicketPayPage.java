package me.jiangcai.dating.page.sale;

import com.google.common.base.Predicate;
import me.jiangcai.dating.page.AbstractPage;
import me.jiangcai.dating.page.ShowOrderPage;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

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
        assertTitle("确认订单");
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
                    input -> input != null && Arrays.asList("收款二维码", "支付宝支付", "微信支付").contains(input.getTitle()));
        } catch (TimeoutException exception) {
            payButton.click();
            try {
                new WebDriverWait(webDriver, 5).until((Predicate<WebDriver>)
                        input -> input != null && Arrays.asList("收款二维码", "支付宝支付", "微信支付").contains(input.getTitle()));
            } catch (TimeoutException e) {
                printThisPage();
                throw e;
            }
        }

        initPage(ShowOrderPage.class).pay();
        return initPage(TicketPaySuccessPage.class);
    }
}
