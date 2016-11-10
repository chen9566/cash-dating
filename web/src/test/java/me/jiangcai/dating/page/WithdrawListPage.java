package me.jiangcai.dating.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 提现明细页面
 * Cashlist.html
 *
 * @author CJ
 */
public class WithdrawListPage extends AbstractPage {

    private List<WithdrawFlow> withdrawFlows;

    public WithdrawListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("提现明细");
    }

    private WithdrawFlow toWithdrawFlow(WebElement element) {
        List<WebElement> texts = element.findElements(By.tagName("li"));
        return new WithdrawFlow(
                texts.get(0).getText()
                , texts.get(1).getText()
                , texts.get(2).getText()
        );
    }

    public void assertList(List<BalanceFlow> withdrawalList) {

        withdrawFlows = webDriver.findElement(By.className("yjlist")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg".equals(webElement.getAttribute("class")))
                .map(this::toWithdrawFlow)
                .collect(Collectors.toList());
        // 流水
        assertThat(withdrawFlows)
                .hasSize(withdrawalList.size());

        ArrayList<WithdrawFlow> withdrawFlows = new ArrayList<>(this.withdrawFlows);
        assertThat(withdrawFlows.stream()
                .filter(webFlow -> !webFlow.inList(withdrawalList))
                .count()).isEqualTo(0);

    }

    /**
     * 提现明细
     */
    @Data
    @AllArgsConstructor
    private class WithdrawFlow {
        private String time;
        private String amount;
        private String status;

        boolean inList(List<BalanceFlow> list) {
            return list.stream()
                    .filter(this::equalsTo)
                    .findAny()
                    .isPresent();
        }

        private boolean equalsTo(BalanceFlow flow) {
            // 金额 状态
            if (!amount.equals(Common.CurrencyFormat(flow.getAmount())))
                return false;
//            if (!comment.equals(balanceFlow.getComment()))
//                return false;
            if (!status.equals(flow.getStatus()))
                return false;
            // time 就算了
            return true;
        }
    }
}
