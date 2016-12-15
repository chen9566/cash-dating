package me.jiangcai.dating.page;

import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.Random;
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

    // 项目贷款专用
    private WebElement limitYears;
    private WebElement termDays;

    public LoanAmountPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isIn("款爷借款", "项目贷款", "网商宝")
//                .isEqualTo("款爷借款")
        ;
        input.clear();
        input.sendKeys(RandomStringUtils.randomAlphabetic(1));
        assertThat(button.isEnabled())
                .isFalse();
//        input.clear();
//        input.sendKeys(RandomStringUtils.randomAscii(1));
//        assertThat(button.isEnabled())
//                .isFalse();
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
                .contains("合同");
        webDriver.navigate().back();
        PageFactory.initElements(webDriver, this);
        validatePage();
    }

    /**
     * 确定现在展示的信息符合这个产品的说明
     *
     * @param loan
     * @param nextTerm 项目贷款专有
     */
    public void assertLoan(Loan loan, int nextTerm) {
        Random random = new Random();
        // 输入最小额可以
        input.clear();
        input.sendKeys(String.valueOf(loan.getMinAmount()));
        assertThat(button.isEnabled())
                .isTrue();
        // 输入最大额 可以
        input.clear();
        input.sendKeys(String.valueOf(loan.getAmountInteger()));
        assertThat(button.isEnabled())
                .isTrue();
        // 中间额可以
        input.clear();

        input.sendKeys(String.valueOf(loan.getMinAmount() + random.nextInt(loan.getAmountInteger() - loan.getMinAmount())));
        assertThat(button.isEnabled())
                .isTrue();
        // 比最早的小 不可以
        input.clear();
        input.sendKeys(String.valueOf(loan.getMinAmount() - 1));
        assertThat(button.isEnabled())
                .isFalse();
        // 比最大的大不可以
        input.clear();
        input.sendKeys(String.valueOf(loan.getAmountInteger() + 1));
        assertThat(button.isEnabled())
                .isFalse();

        if (loan instanceof ProjectLoan) {
            assertThat(limitYears.getText())
                    .contains("借款授信期限" + getSystemService().getProjectLoanCreditLimit() + "年");
            assertThat(termDays.getText())
                    .contains(nextTerm + "天");
        } else {
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

    }

    /**
     * 开始借款啦
     *
     * @param amount
     * @param term
     * @return 详情提交页面
     */
    public LoanSubmitPage loan(int amount, String term) {
        if (term != null)
            inputSelect(webDriver.findElement(By.tagName("form")), period.getAttribute("name"), term);
        input.clear();
        input.sendKeys("" + amount);
        button.click();
        return initPage(LoanSubmitPage.class);
    }
}
