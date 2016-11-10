package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款申请完成
 * persoanlok
 *
 * @author CJ
 */
public class LoanCompletedPage extends AbstractPage {

    private WebElement submit;

    public LoanCompletedPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("申请完成");
    }

    public MyPage doBack() {
        submit.click();
        return initPage(MyPage.class);
    }
}
