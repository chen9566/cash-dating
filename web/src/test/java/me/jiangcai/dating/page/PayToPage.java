package me.jiangcai.dating.page;

import me.jiangcai.dating.model.PayChannel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 向人支付页面
 *
 * @author CJ
 */
public class PayToPage extends AbstractPage {

    private WebElement amount;
    @FindBy(css = "[type=submit]")
    private WebElement button;

    public PayToPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("付款");
    }

    /**
     * 发起订单
     *  @param amount 金额
     * @param pay    是否完成支付
     * @param channel
     */
    public void pay(String amount, boolean pay, PayChannel channel) throws Exception {
        this.amount.sendKeys(amount);
        button.click();

        ShowOrderPage page = initPage(ShowOrderPage.class);
        if (pay)
            page.pay(channel);
    }
}
