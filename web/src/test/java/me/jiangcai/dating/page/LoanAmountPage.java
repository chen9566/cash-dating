package me.jiangcai.dating.page;

import me.jiangcai.dating.model.trj.Loan;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款提现金额和期限的界面
 * loan.html
 *
 * @author CJ
 */
public class LoanAmountPage extends AbstractPage {

    /**
     * 关于限额的说明
     */
    private WebElement limitSpan;
    /**
     * 周期选择
     */
    private WebElement period;
    /**
     * 填写金额的输入框
     */
    @FindBy(id = "_content")
    private WebElement input;
    /**
     * 按钮
     */
    @FindBy(id = "_btn1")
    private WebElement button;

    public LoanAmountPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("款爷借款");
    }

    /**
     * 点击下借款协议,然后再回来
     */
    public void checkAgreement() {
        webDriver.findElements(By.tagName("a")).stream()
                .filter(webElement -> webElement.getText().contains("协议"))
                .findFirst()
                .ifPresent(WebElement::click);
        assertThat(webDriver.getTitle())
                .contains("协议");
        webDriver.navigate().back();
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 确定现在展示的信息符合这个产品的说明
     *
     * @param loan
     */
    public void assertLoan(Loan loan) {
        assertThat(limitSpan.isDisplayed())
                .isTrue();
        assertThat(limitSpan.getText())
                .contains(loan.getAmountInteger() + "元");

        List<String> terms = period.findElements(By.tagName("option")).stream()
                .map(webElement -> webElement.getText().trim())
                .collect(Collectors.toList());

        assertThat(terms)
                .containsOnly(loan.getTerm());
    }

    /**
     * 开始借款啦
     *
     * @param amount
     * @param term
     * @return 详情提交页面
     */
    public LoanSubmitPage loan(int amount, String term) {
        inputSelect(webDriver.findElement(By.tagName("form")), period.getAttribute("name"), term);
        input.clear();
        input.sendKeys("" + amount);
        button.click();
        return initPage(LoanSubmitPage.class);
    }
}
