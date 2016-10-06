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

    private WebElement headImage;
    private WebElement title;
    private WebElement message;
    private final Map<String, WebElement> menus = new HashMap<>();

    public MyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
//        System.out.println(webDriver.getPageSource());
        WebElement my = webDriver.findElement(By.className("my"));

        my.findElements(By.tagName("img")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> headImage = element);

        my.findElements(By.tagName("h4")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> title = element);

        my.findElements(By.tagName("span")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> message = element);


        WebElement list = webDriver.findElement(By.className("my-list"));
        list.findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .forEach(element -> menus.put(element.getText(), element));

        assertThat(headImage)
                .isNotNull();
        assertThat(headImage.isDisplayed())
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
        menus.get(text).click();
    }
}
