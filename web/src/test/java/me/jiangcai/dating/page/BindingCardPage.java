package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.Bank;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Random;
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
    private WebElement subBranchInput;
    private WebElement numberInput;
    private WebElement submitButton;

    public BindingCardPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        getWorkingInputs()
                .filter(element -> element.getAttribute("placeholder").contains("姓名"))
                .findFirst()
                .ifPresent(element -> nameInput = element);

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

        getWorkingInputs()
                .filter(element -> element.getAttribute("placeholder").contains("支行"))
                .findFirst()
                .ifPresent(element -> subBranchInput = element);

        getWorkingInputs()
                .filter(element -> element.getAttribute("placeholder").contains("卡号"))
                .findFirst()
                .ifPresent(element -> numberInput = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> "提交".equals(element.getText()))
                .findFirst()
                .ifPresent(element -> submitButton = element);


        assertThat(nameInput).isNotNull();
        assertThat(nameInput.isDisplayed()).isTrue();
        assertThat(provinceSelect).isNotNull();
        assertThat(citySelect).isNotNull();
        assertThat(bankSelect).isNotNull();
        assertThat(subBranchInput).isNotNull();
        assertThat(numberInput).isNotNull();
        assertThat(submitButton).isNotNull();
    }

    private Stream<WebElement> getWorkingSelects() {
        return webDriver.findElements(By.tagName("select")).stream()
                .filter(WebElement::isDisplayed);
    }

    private Stream<WebElement> getWorkingInputs() {
        return webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed);
    }

    /**
     * 地址自行随机
     *
     * @param bank      银行
     * @param name      姓名
     * @param subBranch 支行
     * @param number    卡号
     */
    public void submitWithRandomAddress(Bank bank, String name, String subBranch, String number) {
        nameInput.clear();
        nameInput.sendKeys(name);

        String[][] s1 = new String[][]{
                new String[]{
                        "浙江", "宁波市"
                }, new String[]{
                "云南", "丽江市"
        }
        };

        WebElement form = webDriver.findElement(By.tagName("form"));
        String[] address = s1[new Random().nextInt(s1.length)];

        inputSelect(form,provinceSelect.getAttribute("name"),address[0]);
        inputSelect(form,citySelect.getAttribute("name"),address[1]);
        //
        inputSelect(form,bankSelect.getAttribute("name"),bank.getName());

        subBranchInput.clear();
        subBranchInput.sendKeys(subBranch);
        numberInput.clear();
        numberInput.sendKeys(number);

        submitButton.submit();
    }
}
