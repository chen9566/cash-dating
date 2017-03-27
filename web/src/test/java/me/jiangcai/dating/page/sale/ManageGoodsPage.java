package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public class ManageGoodsPage extends AbstractPage {
    public ManageGoodsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("商品管理");
    }
}
