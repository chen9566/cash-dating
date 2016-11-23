package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 邀请列表页面
 * 显示输入参数者旗下邀请的人,这个人必须是本人或者是自己邀请的人
 * Invitelist.html
 *
 * @author CJ
 */
public class InviteListPage extends AbstractPage {
    public InviteListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("邀请人");
    }

    public PartnerDataPage partnerDataPage(String nickName) {
//        checkFlows();
//        webFlows.stream()
//                .filter(webFlow -> webFlow.name.equals(nickName))
//                .findFirst()
//                .ifPresent(webFlow -> webFlow.element.click());
        return initPage(PartnerDataPage.class);
    }
}
