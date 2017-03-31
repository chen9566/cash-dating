package me.jiangcai.dating.page.mall;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.util.StringUtils;

/**
 * 商城登录页面
 * /mall/login.html
 *
 * @author CJ
 */
public class LoginPage extends AbstractMallPage {

    private WebElement username;
    private WebElement password;
    @FindBy(css = "[type=submit]")
    private WebElement submitButton;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("用户登录");
    }

    public AbstractMallPage loginAs(String mobile, String password) {
        this.username.clear();
        this.username.sendKeys(mobile);

        this.password.clear();
        this.password.sendKeys(password);

        this.submitButton.click();

        if (StringUtils.isEmpty(webDriver.findElement(By.tagName("body")).getAttribute("data-id"))) {
            return initPage(IndexPage.class);
        }
        return initPage(FakeGoodsDetailPage.class);
    }
}
