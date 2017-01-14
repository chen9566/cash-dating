package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * pay-ts2.html
 *
 * @author CJ
 */
public class TicketPaySuccessPage extends AbstractPage {

    private WebElement detailLink;

    public TicketPaySuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("支付成功");
    }

    /**
     * @return 点击详情
     */
    public TicketTradeSuccessPage detail() {
        detailLink.click();
        return initPage(TicketTradeSuccessPage.class);
    }
}
