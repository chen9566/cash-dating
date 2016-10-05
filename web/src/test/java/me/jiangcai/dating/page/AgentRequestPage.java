package me.jiangcai.dating.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 申请代理商页面
 *
 * @author CJ
 */
public class AgentRequestPage extends AbstractPage {
    private WebElement nameInput;
    private WebElement mobileInput;
    private WebElement submitButton;

    public AgentRequestPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
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
                .filter(element -> element.getText().contains("提交"))
                .findFirst()
                .ifPresent(element -> submitButton = element);
    }

    public void submitRequest(String name, String mobile) {
        this.nameInput.clear();
        nameInput.sendKeys(name);
        mobileInput.clear();
        mobileInput.sendKeys(mobile);
        submitButton.click();
    }
}
