package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.StatisticService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的首页
 *
 * @author CJ
 */
public class MyPage extends AbstractPage {

    private final Map<String, WebElement> menus = new HashMap<>();
    private WebElement headImage;
    private WebElement title;
    private WebElement message;
    private WebElement toPayButton;

    public MyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
//        System.out.println(webDriver.getPageSource());
        WebElement my = webDriver.findElement(By.className("my-n"));

        my.findElements(By.tagName("img")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> headImage = element);

        my.findElements(By.tagName("h1")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> title = element);

        my.findElements(By.tagName("span")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> message = element);

        my.findElements(By.tagName("a")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> "toPay".equals(webElement.getAttribute("name")))
                .findFirst()
                .ifPresent(element -> toPayButton = element);

        webDriver.findElement(By.className("mykey")).findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .forEach(element -> menus.put(element.getText(), element));
        webDriver.findElement(By.className("mylist")).findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .forEach(element -> menus.put(element.getText(), element));

        assertThat(headImage)
                .isNotNull();
        assertThat(headImage.isDisplayed())
                .isTrue();
        assertThat(toPayButton.isDisplayed())
                .isTrue();
        assertThat(title)
                .isNotNull();
        assertThat(message)
                .isNotNull();
        assertThat(menus)
                .isNotEmpty();
    }


    /**
     * 这个页面显示的是这个用户的信息
     *
     * @param user
     * @param statisticService
     */
    public void assertFrom(User user, StatisticService statisticService) {
        assertThat(title.getText())
                .isEqualTo(user.getNickname());
        assertThat(headImage.getAttribute("src"))
                .isEqualTo(user.getHeadImageUrl());
//        <t>在淘宝用了500元</t>
        String text = String.valueOf(statisticService.totalExpense(user.getOpenId()));
        assertThat(message.getText())
                .contains(text);

    }

    public void clickMenu(String text) {
        menus.forEach((name, ele) -> {
            if (name.startsWith(text)) {
                ele.findElements(By.tagName("a")).forEach(WebElement::click);
//                ele.click();
            }

        });
    }

    /**
     * 登出
     */
    public LogoutPage logout() {
        return clickMyData().logout();
//        webDriver.findElements(By.tagName("input")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(webElement -> "注销".equals(webElement.getAttribute("value")))
//                .findFirst()
//                .orElseThrow(NullPointerException::new)
//                .click();
    }

    public MyDataPage clickMyData() {
        headImage.click();
        return initPage(MyDataPage.class);
    }

    public void clickPayToMe() {
        toPayButton.click();
    }

    public FinancingPage toFinancingPage() {
        clickMenu("款爷理财");
//        webDriver.findElements(By.tagName("a")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(webElement -> webElement.getText().contains("款爷理财"))
//                .findFirst()
//                .orElseThrow(IllegalStateException::new)
//                .click();
        return initPage(FinancingPage.class);
    }

    public LoanPage toLoanPage() {
        clickMenu("款爷借款");
        return initPage(LoanPage.class);
    }

    public CodePage toCodePage() {
        clickMenu("邀请好友");
        return initPage(CodePage.class);
    }
}
