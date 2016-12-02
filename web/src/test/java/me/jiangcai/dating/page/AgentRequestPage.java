package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 申请代理商页面
 * agent.html
 *
 * @author CJ
 */
public class AgentRequestPage extends AbstractPage {
    private WebElement nameInput;
    private WebElement mobileInput;
    private WebElement submitButton;
    @FindBy(id = "myAlert")
    private WebElement myAlert;

    public AgentRequestPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("升级合伙人");
        printThisPage();
        webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> "name".equals(element.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> nameInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> "mobile".equals(element.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> mobileInput = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("提交申请"))
                .findFirst()
                .ifPresent(element -> submitButton = element);
    }

    public void submitRequest(String name, String mobile) {
        this.nameInput.clear();
        nameInput.sendKeys(name);
        mobileInput.clear();
        mobileInput.sendKeys(mobile);
        submitButton.click();
        // 确保没有错误窗口弹出
        if (myAlert.isDisplayed()) {
            throw new AssertionError(myAlert.getText());
        }
        assertThat(myAlert.isDisplayed())
                .isFalse();
    }

    public ExplainPage toExplainPage() {
        webDriver.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("合伙人收益说明"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到合伙人收益说明链接"))
                .click();
        return initPage(ExplainPage.class);
    }
}
