package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.BookRateLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MyTeamPage extends AbstractPage {

    private final List<MyTeamMember> memberList = new ArrayList<>();

    public MyTeamPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void assertMember(int index, User user) {
        MyTeamMember myTeamMember = memberList.get(index);
        if (user.getNickname() == null)
            assertThat(myTeamMember.name.getText())
                    .isEmpty();
        else
            assertThat(myTeamMember.name.getText())
                    .isEqualTo(user.getNickname());

        if (user.getMobileNumber() == null)
            assertThat(myTeamMember.mobile.getText()).isEmpty();
        else
            assertThat(myTeamMember.mobile.getText())
                    .isEqualTo(user.getMobileNumber());
        // 手续费
//        printThisPage();
//        System.out.println(myTeamMember.select.getText());
        String showed = myTeamMember.select.findElements(By.tagName("option")).stream()
                .filter(WebElement::isSelected)
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getText();
//        String showed = myTeamMember.select.getText();
        if (user.getMyAgentInfo() == null) {
            // 默认的
            assertThat(showed).isEqualTo("0.6%");
        } else {
            assertThat(showed).isEqualTo(user.getMyAgentInfo().getBookLevel().toString());
        }
    }

    public void changeLevel(int index, BookRateLevel level) {
        MyTeamMember myTeamMember = memberList.get(index);

        myTeamMember.click(level);
    }

    private MyTeamMember To(WebElement webElement) {
        List<WebElement> lis = webElement.findElements(By.tagName("li"));
        MyTeamMember member = new MyTeamMember();
        member.name = lis.get(0);
        member.mobile = lis.get(1);
        member.select = lis.get(2);
        return member;
    }

    @Override
    public void validatePage() {
        memberList.clear();

        assertThat(webDriver.getTitle())
                .isEqualTo("设置支付手续费");

        // 等待 知道没有 a 为止
        WebDriverWait wait = new WebDriverWait(webDriver, 10);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                return input != null && input.findElements(By.cssSelector("a.next")).isEmpty();
            }
        });

//        printThisPage();

        webDriver.findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg".equalsIgnoreCase(webElement.getAttribute("class")))
                .map(this::To)
                .forEach(memberList::add);
    }

    public void assertTeamSize(int size) {
        assertThat(memberList)
                .hasSize(size);
    }

    @Data
    private class MyTeamMember {
        private WebElement name;
        private WebElement mobile;
        private WebElement select;


        public void click(BookRateLevel level) {
            select.findElements(By.tagName("option")).stream()
                    .filter(webElement -> webElement.getText().equals(level.toString()))
                    .findFirst()
                    .ifPresent(WebElement::click);
        }
    }
}
