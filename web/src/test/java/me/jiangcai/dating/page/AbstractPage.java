package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

/**
 * @author CJ
 */
public abstract class AbstractPage extends me.jiangcai.lib.test.page.AbstractPage{

    public AbstractPage(WebDriver webDriver) {
        super(webDriver);
    }

    protected void assertAlert(String text) {
    }
}
