package me.jiangcai.dating.page.mall;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * index.html
 *
 * @author CJ
 */
public class IndexPage extends AbstractMallPage {

    public IndexPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("款爷商城");
    }
}
