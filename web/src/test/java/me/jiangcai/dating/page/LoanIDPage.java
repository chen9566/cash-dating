package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款身份证上传页面
 * id.html
 *
 * @author CJ
 */
public class LoanIDPage extends AbstractPage {
    public LoanIDPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("上传身份证照片");
    }

    public BindingCardPage next() {
        // TODO do something
        return initPage(BindingCardPage.class);
    }
}
