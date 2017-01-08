package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 卡券类商品详情页面
 * carddetails.html
 *
 * @author CJ
 */
public class TicketGoodsDetailPage extends AbstractPage {

    public TicketGoodsDetailPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        printThisPage();
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("商品详情");
    }
}
