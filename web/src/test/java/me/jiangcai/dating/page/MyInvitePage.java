package me.jiangcai.dating.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.BalanceFlow;
import me.jiangcai.dating.service.StatisticService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的邀请
 *
 * @author CJ
 */
public class MyInvitePage extends AbstractPage {
    private WebElement balanceText;
    private WebElement codeButton;
    private WebElement withdrawButton;
    private List<WebFlow> webFlows;

    public MyInvitePage(WebDriver webDriver) {
        super(webDriver);
        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.className("cri")).stream()
                .filter(WebElement::isDisplayed)
                .findAny()
                .ifPresent(element -> {
                    // 暂时就应该是用b了吧
                    balanceText = element.findElement(By.tagName("b"));
                });

        //withdraw
        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("提现"))
                .findAny()
                .ifPresent(element -> withdrawButton = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("我的邀请码"))
                .findAny()
                .ifPresent(element -> codeButton = element);

        webFlows = webDriver.findElement(By.className("balliist")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .map(this::toFlow)
                .collect(Collectors.toList());

        assertThat(balanceText)
                .isNotNull();
        assertThat(withdrawButton)
                .isNotNull();
        assertThat(codeButton)
                .isNotNull();
//        assertThat(webFlows)
//                .isNotEmpty();
    }

    private WebFlow toFlow(WebElement element) {
        return new WebFlow(
                element.findElement(By.className("flow-amount")).getText()
                , element.findElement(By.className("flow-comment")).getText()
                , element.findElement(By.className("flow-name")).getText()
                , element.findElement(By.className("flow-time")).getText()
        );
    }

    /**
     * 确定是这个用户的页面
     *
     * @param user
     * @param statisticService
     */
    public void assertUser(User user, StatisticService statisticService) {
        String text = String.valueOf(statisticService.balance(user.getOpenId()));
        assertThat(balanceText.getText())
                .isEqualTo(text);

        List<BalanceFlow> list = statisticService.balanceFlows(user.getOpenId());
        // 流水
        assertThat(webFlows)
                .hasSize(list.size());

        ArrayList<WebFlow> webFlowArrayList = new ArrayList<>(webFlows);
        assertThat(webFlowArrayList.stream()
                .filter(webFlow -> !webFlow.inList(list))
                .count()).isEqualTo(0);
    }


    public void clickMyCode() {
        codeButton.click();
    }

    @Data
    @AllArgsConstructor
    private class WebFlow {
        private String amount;
        private String comment;
        private String name;
        private String time;

        /**
         * this 在 这个list里
         *
         * @param list
         * @return
         */
        boolean inList(List<BalanceFlow> list) {
            return list.stream()
                    .filter(this::equalsTo)
                    .findAny()
                    .isPresent();
        }

        private boolean equalsTo(BalanceFlow balanceFlow) {
            String dataAmount = balanceFlow.getFlowType().toFlag() + String.valueOf(balanceFlow.getAmount());
            if (!amount.equals(dataAmount))
                return false;
            if (!comment.equals(balanceFlow.getComment()))
                return false;
            if (!name.equals(balanceFlow.getFlowName()))
                return false;
            // time 就算了
            return true;
        }
    }
}
