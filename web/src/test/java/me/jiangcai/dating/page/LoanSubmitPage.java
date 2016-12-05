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
    private WebElement homeAddress;
    private WebElement employer;
    //    private WebElement profitMonthly;
    private WebElement propose;

    public LoanSubmitPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("基本信息");
    }

    public LoanIDPage submit(String name, String number, String provinceName, String cityName, boolean hasHouse
            , int age, int familyIncome, int personalIncome) {
        long ctime = System.currentTimeMillis();
        inputSelect(loanForm, "hasHouse", hasHouse ? "有" : "无");
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
        inputText(loanForm, "age", String.valueOf(age));
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
        inputText(loanForm, "familyIncome", String.valueOf(familyIncome));
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
        inputText(loanForm, "personalIncome", String.valueOf(personalIncome));
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();

        // 这些字段目前都没有保存
        homeAddress.clear();
        homeAddress.sendKeys("Y");
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
        employer.clear();
        employer.sendKeys("X");
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
//        profitMonthly.clear();
//        profitMonthly.sendKeys(UUID.randomUUID().toString());
//        propose.clear();
//        propose.sendKeys(UUID.randomUUID().toString());


        fillCommonForm(name, number, provinceName, cityName);
        System.out.println("cost" + (System.currentTimeMillis() - ctime) + "ms");
        ctime = System.currentTimeMillis();
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
