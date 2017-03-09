package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * note.html
 *
 * @author CJ
 */
public class InvitationNotePage extends AbstractPage {
    @FindBy(id = "numbers")
    private WebElement numbers;

    public InvitationNotePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("邀请说明");
    }

    public void assertNumber(int i) {
        assertThat(numbers.getText())
                .isEqualTo(String.valueOf(i));
    }
}
