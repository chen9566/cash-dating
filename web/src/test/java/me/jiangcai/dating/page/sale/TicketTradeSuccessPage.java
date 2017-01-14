package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * cardplayfinish.html 订单详情
 *
 * @author CJ
 */
public class TicketTradeSuccessPage extends AbstractPage {
    public TicketTradeSuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("订单详情");
    }
}
