package me.jiangcai.dating.page;

import me.jiangcai.dating.model.trj.Financing;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class FinancingPage extends AbstractPage {

    private WebElement financingYearRate;
    private WebElement financingName;
    private WebElement financingDesc;

    public FinancingPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("款爷理财");
    }

    public void assertFinancing(Financing financing) {
        // projectTypeName
        // yearRate
        // sologan
        System.out.println(financing);
        System.out.println(financingYearRate.getText());
        System.out.println(financingName.getText());
        System.out.println(financingDesc.getText());
        assertThat(financingYearRate.getText().replaceAll(" ", ""))
                .contains(financing.getYearRate());
        assertThat(financingName.getText())
                .contains(financing.getProjectTypeName());
        if (financing.getProjectSlogan() != null)
            assertThat(financingDesc.getText())
                    .contains(financing.getProjectSlogan());
    }

    public void goFinancing() {
        financingName.click();
    }

    public void assertLoginPage() {
        assertThat(webDriver.getCurrentUrl())
                .contains("login");
    }

    public void assertWorkingPage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("投融家");
        assertThat(webDriver.getCurrentUrl())
                .doesNotContain("login");
    }
}
