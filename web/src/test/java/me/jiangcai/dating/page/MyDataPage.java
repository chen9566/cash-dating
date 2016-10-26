package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MyDataPage extends AbstractPage {

    private WebElement headImage;
    private WebElement nameSpan;
    private WebElement mobileSpan;
    private WebElement toPayButton;

    public MyDataPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("我的");

        List<WebElement> rights = webDriver.findElements(By.cssSelector("span.pull-right"));

        headImage = rights.get(0).findElements(By.tagName("img")).get(0);
        nameSpan = rights.get(1);
        mobileSpan = rights.get(2);
        toPayButton = rights.get(3);// 有疑问


        assertThat(headImage.isDisplayed()).isTrue();
        assertThat(nameSpan.isDisplayed()).isTrue();
        assertThat(mobileSpan.isDisplayed()).isTrue();
        assertThat(toPayButton.isDisplayed()).isTrue();
    }

    /**
     * 登出
     */
    public void logout() {
        webDriver.findElements(By.tagName("input")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> "注销".equals(webElement.getAttribute("value")))
                .findFirst()
                .orElseThrow(NullPointerException::new)
                .click();
    }

    public void assertUser(User user) {
        assertThat(nameSpan.getText())
                .isEqualTo(user.getNickname());
        assertThat(mobileSpan.getText())
                .isEqualTo(user.getMobileNumber());
        assertThat(headImage.getAttribute("src"))
                .isEqualTo(user.getHeadImageUrl());
    }
}
