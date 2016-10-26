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
    private WebElement balanceText;
    //    private WebElement codeButton;
    private WebElement withdrawButton;
    private WebElement explainButton;
    private WebElement teamButton;
    private List<WebFlow> webFlows;

    public MyInvitationPage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {

        assertThat(webDriver.getTitle())
                .isEqualTo("我的邀请");

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

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().equals("我的邀请码"))
//                .findAny()
//                .ifPresent(element -> codeButton = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("合伙佣金"))
                .findAny()
                .ifPresent(element -> teamButton = element);

        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().contains("合伙人"))
                .findAny()
                .ifPresent(element -> explainButton = element);

        webFlows = webDriver.findElement(By.className("yjlist")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg" .equals(webElement.getAttribute("class")))
                .map(this::toFlow)
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

    private WebFlow toFlow(WebElement element) {
        List<WebElement> texts = element.findElements(By.tagName("li"));
        return new WebFlow(
                texts.get(3).getText()
                , null
                , texts.get(1).getText()
                , texts.get(0).getText()
        );
    }

    /**
     * 确定是这个用户的页面
     *
     * @param user
     * @param statisticService
     */
    public void assertUser(User user, StatisticService statisticService) {
//        printThisPage();
        String text = NumberUtils.format(statisticService.balance(user.getOpenId()), 1, NumberPointType.COMMA, 0, NumberPointType.POINT, Locale.CHINA);
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
//        codeButton.click();
        explainButton.click();
        ExplainPage explainPage = initPage(ExplainPage.class);

        explainPage.clickMyCode();
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
