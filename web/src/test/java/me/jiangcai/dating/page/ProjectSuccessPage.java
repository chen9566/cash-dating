package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 项目贷款成功之后的页面
 * loansuccess.html
 *
 * @author CJ
 */
public class ProjectSuccessPage extends AbstractPage {
    public ProjectSuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("确认借款");
        printThisPage();
    }
}
