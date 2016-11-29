package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManageOrderPage extends AbstractPage {
    private WebElement search;
    @FindBy(css = "input[type=submit]")
    private WebElement submit;

    public ManageOrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .contains("订单查询");
    }

    public void search(String s) {
        search.clear();
        search.sendKeys(s);
        submit.click();
    }
}
