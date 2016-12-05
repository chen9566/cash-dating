package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 上传手持身份证的照片
 * handid.html
 *
 * @author CJ
 */
public class LoanHandIDPage extends AbstractPage {

    public LoanHandIDPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("上传身份证照片");
    }
}
