package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.UUID;

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
    private WebElement homeAddress;
    private WebElement employer;
    private WebElement profitMonthly;
    private WebElement propose;

    public LoanSubmitPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("基本信息");
    }

    public LoanIDPage submit(String name, String number, String provinceName, String cityName) {


        // 这些字段目前都没有保存
        homeAddress.clear();
        homeAddress.sendKeys(UUID.randomUUID().toString());
        employer.clear();
        employer.sendKeys(UUID.randomUUID().toString());
        profitMonthly.clear();
        profitMonthly.sendKeys(UUID.randomUUID().toString());
        propose.clear();
        propose.sendKeys(UUID.randomUUID().toString());


        fillCommonForm(name, number, provinceName, cityName);
        return initPage(LoanIDPage.class);
    }

    private void fillCommonForm(String name, String number, String provinceName, String cityName) {
        this.name.clear();
        this.name.sendKeys(name);
        this.number.clear();
        this.number.sendKeys(number);
        inputSelect(loanForm, province.getAttribute("name"), provinceName);
        inputSelect(loanForm, city.getAttribute("name"), cityName);

        submitButton.click();
    }

    /**
     * 提交普通借款
     *
     * @param name
     * @param number
     * @param provinceName
     * @param cityName
     * @return
     */
    public LoanCompletedPage submitNormal(String name, String number, String provinceName, String cityName) {
        fillCommonForm(name, number, provinceName, cityName);
        return initPage(LoanCompletedPage.class);
    }
}
