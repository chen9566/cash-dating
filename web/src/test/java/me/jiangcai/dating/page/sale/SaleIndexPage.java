package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 特卖首页
 * saleindex.html
 *
 * @author CJ
 */
public class SaleIndexPage extends AbstractPage {

    public SaleIndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("特卖");

        try {
            Thread.sleep(1500L);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 打开这个详情
     *
     * @param goods 商品
     * @return 页面
     */
    public TicketGoodsDetailPage clickTicketGoods(CashGoods goods) {
        webDriver.findElements(By.className("cashGoods")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains(goods.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到" + goods.getName()))
                .click();
        return initPage(TicketGoodsDetailPage.class);
    }
}
