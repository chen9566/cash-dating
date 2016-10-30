package me.jiangcai.dating.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.thymeleaf.util.NumberPointType;
import org.thymeleaf.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的邀请
 *
 * @author CJ
 */
public class MyInvitationPage extends AbstractPage {
    private WebElement agentElement;
    @FindBy(css = "span[name=balance]")
    private WebElement balanceText;
    //    private WebElement codeButton;
    private WebElement withdrawButton;
    private WebElement teamButton;
    private List<WebFlow> webFlows;
    @FindBy(css = "span[name=numbers]")
    private WebElement numbersText;
    private List<WithdrawFlow> withdrawFlows;

    public MyInvitationPage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {

        assertThat(webDriver.getTitle())
                .isEqualTo("我的邀请");

        agentElement = webDriver.findElements(By.tagName("span")).stream()
//                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().contains("成为超级合伙人"))
                .findFirst().orElse(null);

//        List<WebElement> numbers = webDriver.findElements(By.cssSelector("p.num"));

//        balanceText  = numbers.get(0);
//        numbersText  = numbers.get(1);
//        webDriver.findElements(By.className("cri")).stream()
//                .filter(WebElement::isDisplayed)
//                .findAny()
//                .ifPresent(element -> {
//                    // 暂时就应该是用b了吧
//                    balanceText = element.findElement(By.tagName("b"));
//                });

        //withdraw
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("提现"))
                .findAny()
                .ifPresent(element -> withdrawButton = element);

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().equals("我的邀请码"))
//                .findAny()
//                .ifPresent(element -> codeButton = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> "调整手续费".equals(element.getAttribute("value")))
                .findAny()
                .ifPresent(element -> teamButton = element);

//        webDriver.findElements(By.tagName("a")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().contains("合伙人"))
//                .findAny()
//                .ifPresent(element -> explainButton = element);

        webFlows = webDriver.findElement(By.id("yj")).findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg".equals(webElement.getAttribute("class")))
                .map(this::toFlow)
                .collect(Collectors.toList());

        withdrawFlows = webDriver.findElement(By.id("tx")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg".equals(webElement.getAttribute("class")))
                .map(this::toWithdrawFlow)
                .collect(Collectors.toList());


        assertThat(balanceText)
                .isNotNull();
        assertThat(balanceText.isDisplayed())
                .isTrue();
        assertThat(withdrawButton)
                .isNotNull();
//        assertThat(codeButton)
//                .isNotNull();
//        assertThat(webFlows)
//                .isNotEmpty();
    }

    private WithdrawFlow toWithdrawFlow(WebElement element) {
        List<WebElement> texts = element.findElements(By.tagName("li"));
        return new WithdrawFlow(
                texts.get(0).getText()
                , texts.get(1).getText()
                , texts.get(2).getText()
        );
    }

    private WebFlow toFlow(WebElement element) {
        List<WebElement> texts = element.findElements(By.tagName("span"));
        return new WebFlow(
                element.findElement(By.className("f15")).getText()
                , null
                , texts.get(0).getText()
                , texts.get(2).getText()
        );
    }

    /**
     * 确定是这个用户的页面
     *
     * @param user
     * @param statisticService
     */
    public void assertUser(User user, StatisticService statisticService) {

        if (user.getAgentInfo() != null)
            assertThat(agentElement).isNull();
        else
            assertThat(agentElement.isDisplayed()).isTrue();

//        printThisPage();
        String text = NumberUtils.format(statisticService.balance(user.getOpenId()), 1, NumberPointType.COMMA, 2, NumberPointType.POINT, Locale.CHINA);
        assertThat(balanceText.getText())
                .startsWith(text);
        // 人数
        assertThat(numbersText.getText())
                .startsWith("" + statisticService.guides(user.getOpenId()));

        List<BalanceFlow> list = statisticService.commissionFlows(user.getOpenId());
        // 流水
        assertThat(webFlows)
                .hasSize(list.size());

        ArrayList<WebFlow> webFlowArrayList = new ArrayList<>(webFlows);
        assertThat(webFlowArrayList.stream()
                .filter(webFlow -> !webFlow.inList(list))
                .count()).isEqualTo(0);

        List<BalanceFlow> withdrawalList = statisticService.withdrawalFlows(user.getOpenId());
        // 流水
        assertThat(withdrawFlows)
                .hasSize(withdrawalList.size());

        ArrayList<WithdrawFlow> withdrawFlows = new ArrayList<>(this.withdrawFlows);
        assertThat(withdrawFlows.stream()
                .filter(webFlow -> !webFlow.inList(withdrawalList))
                .count()).isEqualTo(0);


    }


    public void toRequestAgentPage() {
//        codeButton.click();
        agentElement.click();
//        ExplainPage explainPage = initPage(ExplainPage.class);
//
//        explainPage.requestAgent();
    }

    public void assertNoTeam() {
        if (teamButton != null) {
            try {
                assertThat(teamButton.isDisplayed())
                        .isFalse();
            } catch (NoSuchElementException ignored) {

            }
        }
    }

    public void assertTeam() {
        assertThat(teamButton.isDisplayed())
                .isTrue();
    }

    public void clickMyTeam() {
        teamButton.click();
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
            String dataAmount = balanceFlow.getFlowType().toFlag() + Common.CurrencyFormat(balanceFlow.getAmount());
            if (!amount.equals(dataAmount))
                return false;
//            if (!comment.equals(balanceFlow.getComment()))
//                return false;
            if (!name.equals(balanceFlow.getFlowName()))
                return false;
            // time 就算了
            return true;
        }
    }
}
