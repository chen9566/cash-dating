package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.StatisticService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.className("cri")).stream()
                .filter(WebElement::isDisplayed)
                .findAny()
                .ifPresent(element -> balanceText = element);

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
        assertThat(webFlows)
                .isNotEmpty();
    }

    private WebFlow toFlow(WebElement element) {
        return null;
    }

    /**
     * 确定是这个用户的页面
     *
     * @param user
     * @param statisticService
     */
    public void assertUser(User user, StatisticService statisticService) {

    }

    public void clickMyCode() {
        codeButton.click();
    }

    private class WebFlow {
    }
}
