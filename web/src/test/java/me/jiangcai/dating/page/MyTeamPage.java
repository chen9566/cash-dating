package me.jiangcai.dating.page;

import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.BookRateLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        assertThat(myTeamMember.name.getText())
                .isEqualTo(user.getNickname());
        assertThat(myTeamMember.mobile.getText())
                .isEqualTo(user.getMobileNumber());
        if (user.getMyAgentInfo() == null) {
            // 默认的
            assertThat(myTeamMember.select.getText()).isEqualTo("0.6%");
        } else {
            assertThat(myTeamMember.select.getText()).isEqualTo(user.getMyAgentInfo().getBookLevel().toString());
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
        member.mobile = lis.get(0);
        member.select = lis.get(0);
        return member;
    }

    @Override
    public void validatePage() {
        memberList.clear();

        assertThat(webDriver.getTitle())
                .isEqualTo("合伙佣金");

        webDriver.findElements(By.tagName("ul")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> !"bg" .equalsIgnoreCase(webElement.getAttribute("class")))
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
