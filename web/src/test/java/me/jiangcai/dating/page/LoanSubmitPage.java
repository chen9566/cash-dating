package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款最后提交页面
 * personalup.html
 *
 * @author CJ
 */
public class LoanSubmitPage extends AbstractPage {
    private WebElement name;
    private WebElement number;
    @FindBy(className = "province-selector")
    private WebElement province;
    @FindBy(className = "city-selector")
    private WebElement city;
    private WebElement submitButton;
    private WebElement loanForm;

    public LoanSubmitPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("基本信息");
    }

    public LoanIDPage submit(String name, String number, String proviceName, String cityName) {
        this.name.clear();
        this.name.sendKeys(name);
        this.number.clear();
        this.number.sendKeys(number);
        inputSelect(loanForm, province.getAttribute("name"), proviceName);
        inputSelect(loanForm, city.getAttribute("name"), cityName);
        submitButton.click();
        return initPage(LoanIDPage.class);
    }
}
