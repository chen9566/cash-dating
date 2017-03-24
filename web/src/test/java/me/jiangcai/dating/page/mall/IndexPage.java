package me.jiangcai.dating.page.mall;

import org.openqa.selenium.WebDriver;

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
        assertTitle("款爷商城");
    }

}
