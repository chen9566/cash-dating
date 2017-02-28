package me.jiangcai.dating.page;

import me.jiangcai.dating.model.trj.Loan;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 借款主界面
 * loanmain.html
 *
 * @author CJ
 */
public class LoanPage extends AbstractPage {

    private WebElement loanList;

    public LoanPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("款爷借款");
    }

    public void assertList(Loan[] loanList) {
        List<WebElement> products = this.loanList.findElements(By.tagName("ul"));
        assertThat(products)
                .hasSize(loanList.length + 1);

        //把符合条件的都过滤掉
        for (Loan loan : loanList) {
            int size = products.size();
            products = products.stream()
                    .filter(webElement -> {
                        // 把不符合loan的留下来
//                        webElement.findElements(By.tagName("p")).stream()
//                                .map(WebElement::getText)
//                                .map(s -> s.replaceAll(" ",""))
//                                .forEach(System.out::println);

                        boolean haveSameAmount = webElement.findElements(By.tagName("p")).stream()
                                .anyMatch(p -> p.getText().replaceAll(" ", "").contains(loan.getAmount10K() + "万"));
                        boolean haveSameName = webElement.findElements(By.tagName("p")).stream()
                                .anyMatch(p -> p.getText().replaceAll(" ", "").contains(loan.getProductName()));
                        return !haveSameAmount || !haveSameName;
                    }).collect(Collectors.toList());
            assertThat(products.size())
                    .isEqualTo(size - 1);
        }

    }

    public LoanAmountPage choose(String name) {
        webDriver.findElements(By.tagName("ul")).stream()
                .filter(ul -> ul.findElements(By.tagName("p")).stream()
                        .anyMatch(p -> p.getText().replaceAll(" ", "").contains(name)))
                .findFirst()
                .ifPresent(WebElement::click);
        return initPage(LoanAmountPage.class);
    }
}
