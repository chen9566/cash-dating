package me.jiangcai.dating.page;

import org.openqa.selenium.By;
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
//        printThisPage();
    }

    /**
     * 点击这个合同,并且点击确定
     *
     * @param type CT00..
     */
    public void sign(String type) throws InterruptedException {
        webDriver.findElement(By.name("A_" + type)).click();

        ProjectLoanAgreementPage agreementPage = initPage(ProjectLoanAgreementPage.class);

        agreementPage.agree();

        reloadPageInfo();
        assertSign(type);
    }

    public void assertSign(String type) {
        assertThat(webDriver.findElement(By.id(type)).isSelected())
                .isTrue();
    }
}
