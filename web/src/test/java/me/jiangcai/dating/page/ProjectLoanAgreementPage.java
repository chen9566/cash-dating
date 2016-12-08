package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;
import org.openqa.selenium.support.FindBy;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ProjectLoanAgreementPage extends AbstractPage {

    @FindBy(css = "input[class~=chk_1]")
    private WebElement checkbox;

    public ProjectLoanAgreementPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isNotEqualTo("确认借款");
    }
//    @FindBy(css = "input[type=submit]")
//    private WebElement button;

    void agree() throws InterruptedException {
        //其他页面了
        //点上
//        printThisPage();
//        new WebDriverWait(webDriver, 3).until(new Predicate<WebDriver>() {
//            @Override
//            public boolean apply(@Nullable WebDriver input) {
//                if (input == null)
//                    return false;
//                printThisPage();
//                return input.findElement(By.cssSelector("input[class~=chk_1]")).isDisplayed();
//            }
//        });
        // 此处我们强迫修改button的状态!
        WebElement button = webDriver.findElement(By.cssSelector("input[type=submit]"));
        getHtmlElement(button).removeAttribute("disabled");

        button.click();
    }

    private HtmlElement getHtmlElement(WebElement element) {
        try {
            Field field = HtmlUnitWebElement.class.getDeclaredField("element");
            field.setAccessible(true);
            return (HtmlElement) field.get(element);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
