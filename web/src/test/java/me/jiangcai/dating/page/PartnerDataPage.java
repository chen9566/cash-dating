package me.jiangcai.dating.page;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 合伙人资料
 * 显示了这个人在自己这边产生的收款总额 和 自己收到的佣金总额
 * 这个人必须是本人或者是自己邀请的人,必须隶属于本人的team
 * Partnerdata.html
 *
 * @author CJ
 */
public class PartnerDataPage extends AbstractPage {
    public PartnerDataPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("合伙人资料");
    }

    public InviteListPage subListPage() {
        return initPage(InviteListPage.class);
    }
}
