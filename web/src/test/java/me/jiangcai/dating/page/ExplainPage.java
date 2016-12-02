package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * explain.html
 * @author CJ
 */
public class ExplainPage extends AbstractPage {

    private WebElement codeButton;

    public ExplainPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("款爷——你的赚钱平台");

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().contains("邀请"))
                .findFirst()
                .ifPresent(element -> codeButton = element);
        // 这个似乎干不了什么
//
//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().contains("合伙人"))
//                .findFirst()
//                .ifPresent(element -> agentButton = element);

    }

    /**
     * 点击成为合伙人
     */
    public AgentRequestPage requestAgent() {
        webDriver.findElement(By.tagName("button")).click();
        return initPage(AgentRequestPage.class);
    }
}
