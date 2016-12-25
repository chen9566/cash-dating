package me.jiangcai.dating.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.util.Common;
import org.openqa.selenium.By;
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
 * 邀请明细
 *
 * myinvitation.html
 * @author CJ
 */
public class MyInvitationPage extends AbstractPage {
    private WebElement agentElement;
    /**
     * 显示余额的element
     */
    @FindBy(css = "p[name=balance]")
    private WebElement balanceText;
    //    private WebElement codeButton;
    private WebElement withdrawButton;
    private WebElement teamButton;
    private List<WebFlow> webFlows;
    private List<WebFriend> webFriends;
    @FindBy(css = "span[name=numbers]")
    private WebElement numbersText;


    public MyInvitationPage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {

        assertThat(webDriver.getTitle())
                .isEqualTo("我邀请的好友");

        agentElement = webDriver.findElements(By.tagName("a")).stream()
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
        withdrawButton = webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("提现"))
                .findAny()
                .orElse(null);

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().equals("我的邀请码"))
//                .findAny()
//                .ifPresent(element -> codeButton = element);

        teamButton = null;
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


//        assertThat(balanceText)
//                .isNotNull();
//        assertThat(balanceText.isDisplayed())
//                .isTrue();
//        assertThat(withdrawButton)
//                .isNotNull();
//        assertThat(codeButton)
//                .isNotNull();
//        assertThat(webFlows)
//                .isNotEmpty();
    }

    private WebFriend toFriend(WebElement element) {
        return new WebFriend(
                element.findElement(By.className("w2")).getText(),
                element.findElement(By.className("w3")).getAttribute("class").contains("green")
        );
    }

    private WebFlow toFlow(WebElement element) {
        List<WebElement> texts = element.findElements(By.tagName("li"));
        return new WebFlow(
                texts.get(2).getText()
                , null
                , texts.get(0).getText()
                , null
                , element
        );
    }

    public void assertUser(User user, List<User> invited, UserService userService) {
        printThisPage();
        webFriends = webDriver.findElement(By.className("navlist")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
//                .filter(webElement -> !"bg".equals(webElement.getAttribute("class")))
                .map(this::toFriend)
                .collect(Collectors.toList());

        assertThat(webFriends)
                .hasSize(invited.size());

        ArrayList<WebFriend> webFlowArrayList = new ArrayList<>(webFriends);
        assertThat(webFlowArrayList.stream()
                .filter(webFlow -> !webFlow.inList(invited, userService))
                .count()).isEqualTo(0);
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

        checkFlows();

        List<BalanceFlow> list = statisticService.commissionFlows(user.getOpenId());
        // 流水
        assertThat(webFlows)
                .hasSize(list.size());

        ArrayList<WebFlow> webFlowArrayList = new ArrayList<>(webFlows);
        assertThat(webFlowArrayList.stream()
                .filter(webFlow -> !webFlow.inList(list))
                .count()).isEqualTo(0);
//
//        webDriver.findElement(By.cssSelector(".yj[name=tx]")).click();

    }

    private void checkFlows() {
        webDriver.findElement(By.cssSelector(".yj[name=yj]")).click();
        webFlows = webDriver.findElement(By.id("yj")).findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg".equals(webElement.getAttribute("class")))
                .map(this::toFlow)
                .collect(Collectors.toList());
    }


    public AgentRequestPage toRequestAgentPage() {
//        codeButton.click();
        agentElement.click();
//        ExplainPage explainPage = initPage(ExplainPage.class);
//
//        explainPage.requestAgent();
        return initPage(AgentRequestPage.class);
    }

    public void assertNoTeam() {
//        if (teamButton != null) {
//            try {
//                assertThat(teamButton.isDisplayed())
//                        .isFalse();
//            } catch (NoSuchElementException ignored) {
//
//            }
//        }
        assertThat(withdrawButton)
                .isNull();
    }

    public void assertTeam() {
//        assertThat(teamButton.isDisplayed())
//                .isTrue();
        assertThat(withdrawButton)
                .isNotNull();
    }

    /**
     * 1.8.1 以后废止
     */
    @Deprecated
    public void clickMyTeam() {
        teamButton.click();
    }

    public WithdrawPage toWithdrawPage() {
        withdrawButton.click();
        return initPage(WithdrawPage.class);
    }

    /**
     * @return 打开邀请人员列表
     */
    public InviteListPage inviteListPage() {
        numbersText.click();
        return initPage(InviteListPage.class);
    }

    public PartnerDataPage partnerDataPage(String nickName) {
        checkFlows();
        webFlows.stream()
                .filter(webFlow -> webFlow.name.equals(nickName))
                .findFirst()
                .ifPresent(webFlow -> webFlow.element.click());
        return initPage(PartnerDataPage.class);
    }

    @Data
    @AllArgsConstructor
    private class WebFriend {
        private String nickname;
        private boolean valid;

        boolean inList(List<User> list, UserService userService) {
            return list.stream()
                    .filter(user -> this.equalsTo(user, userService))
                    .findAny()
                    .isPresent();
        }

        private boolean equalsTo(User user, UserService userService) {
            if (!nickname.equals(user.getNickname()))
                return false;
            if (userService.isValidUser(user.getOpenId()) != valid)
                return false;
            return true;
        }
    }

    /**
     * 佣金明细
     */
    @Data
    @AllArgsConstructor
    private class WebFlow {
        private String amount;
        private String comment;
        private String name;
        private String time;
        private WebElement element;

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
//            String dataAmount = balanceFlow.getFlowType().toFlag() + Common.CurrencyFormat(balanceFlow.getAmount());
//            String dataAmount = "返佣" + Common.CurrencyFormat(balanceFlow.getAmount()) + "元";
            String dataAmount = Common.CurrencyFormat(balanceFlow.getAmount());
            if (!amount.contains(dataAmount))
                return false;
//            if (!comment.equals(balanceFlow.getComment()))
//                return false;
            if (!name.equals(balanceFlow.getOwner().getNickname()))
                return false;
            // time 就算了
            return true;
        }
    }
}
