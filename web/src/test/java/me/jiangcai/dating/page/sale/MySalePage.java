package me.jiangcai.dating.page.sale;

import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MySalePage extends AbstractPage {
    public MySalePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("我的");
    }

    /**
     * 可用的电子券
     *
     * @param count
     */
    public void assertUsableTicket(int count) {
        assertThat(webDriver.findElement(By.className("nouse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }

    /**
     * 已用过的电子券
     *
     * @param count
     */
    public void assertUsedTicket(int count) {
        assertThat(webDriver.findElement(By.className("douse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }
}
