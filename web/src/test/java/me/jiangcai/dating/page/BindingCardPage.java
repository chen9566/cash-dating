package me.jiangcai.dating.page;

import me.jiangcai.chanpay.model.City;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.service.PayResourceService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 银行卡绑定页面
 *
 * @author CJ
 */
public class BindingCardPage extends AbstractPage {

    private WebElement nameInput;
    private WebElement provinceSelect;
    private WebElement citySelect;
    private WebElement bankSelect;
    //    private WebElement subBranchInput;
    private WebElement numberInput;
    private WebElement submitButton;
    private WebElement subBranchRegion;
    private WebElement ownerId;

    public BindingCardPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        getWorkingSelects()
                .filter(element -> "province".equalsIgnoreCase(element.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> provinceSelect = element);

        getWorkingSelects()
                .filter(element -> "city".equalsIgnoreCase(element.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> citySelect = element);

        getWorkingSelects()
                .filter(element -> "bank".equalsIgnoreCase(element.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> bankSelect = element);

//        getWorkingInputs()
//                .filter(element -> element.getAttribute("placeholder").contains("支行"))
//                .findFirst()
//                .ifPresent(element -> subBranchInput = element);
//        getWorkingSelects()
//                .filter(element -> "subBranch".equalsIgnoreCase(element.getAttribute("name")))
//                .findFirst()
//                .ifPresent(element -> subBranchInput = element);


        checkFormElement();

        assertThat(nameInput).isNotNull();
        assertThat(nameInput.isDisplayed()).isTrue();
        assertThat(provinceSelect).isNotNull();
        assertThat(citySelect).isNotNull();
        assertThat(bankSelect).isNotNull();
//        assertThat(subBranchInput).isNotNull();
        assertThat(numberInput).isNotNull();
        assertThat(submitButton).isNotNull();
        assertThat(submitButton.isDisplayed()).isTrue();
    }

    private void checkFormElement() {
        getWorkingInputs()
                .filter(element -> element.getAttribute("placeholder").contains("姓名"))
                .findFirst()
                .ifPresent(element -> nameInput = element);
        getWorkingInputs()
                .filter(element -> element.getAttribute("placeholder").contains("卡号"))
                .findFirst()
                .ifPresent(element -> numberInput = element);
        webDriver.findElements(By.cssSelector("input[type=submit]")).stream()
                .filter(WebElement::isDisplayed)
//                .filter(element -> "提交".equals(element.getText()))
                .findFirst()
                .ifPresent(element -> submitButton = element);
    }

    private Stream<WebElement> getWorkingSelects() {
        return webDriver.findElements(By.tagName("select")).stream()
                .filter(WebElement::isDisplayed);
    }

    private Stream<WebElement> getWorkingInputs() {
        return webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed);
    }

//    public void submitWithRandomAll() {
//        getTestInstance();
//        SubBranchBank subBranchBank = randomSubBranchBank();
//
//        final String owner = RandomStringUtils.randomAlphanumeric(3);
//        final String number = randomBankCard();
//        submitWithRandomAddress(subBranchBank, owner, number, random);
//    }

    /**
     * @param branchBank
     * @param name
     * @param number
     * @param ownerId
     */
    public void submitWithRandomAddress(SubBranchBank branchBank, String name, String number, String ownerId) {
        // 这下省份了城市都应该有了
        City city = PayResourceService.cityById(branchBank.getCityCode());
        Province province = PayResourceService.provinceByCity(city);

        WebElement form = webDriver.findElement(By.tagName("form"));

        inputSelect(form, provinceSelect.getAttribute("name"), province.getName());
        inputSelect(form, citySelect.getAttribute("name"), city.getName());
        //
        inputSelect(form, bankSelect.getAttribute("name"), branchBank.getBank().getName());

        subBranchRegion.click();

        // 去找目标
        webDriver.findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> branchBank.getName().endsWith(webElement.getText()))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .click();
        checkFormElement();
//        inputSelect(form, subBranchInput.getAttribute("name"), label -> {
//            return branchBank.getName().endsWith(label);
//        });

//        subBranchInput.clear();
//        subBranchInput.sendKeys(subBranch);

        if (ownerId != null) {
            this.ownerId.clear();
            this.ownerId.sendKeys(ownerId);
        }
        numberInput.clear();
        numberInput.sendKeys(number);
        nameInput.clear();
        nameInput.sendKeys(name);

        submitButton.submit();
    }
}
