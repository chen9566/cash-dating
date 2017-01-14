package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.QRCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 合伙賺钱页面
 * 2016-12-25更新至「邀请好友」页面
 * code.html -> friends/inviteFriends.html
 * @author CJ
 */
public class CodePage extends AbstractPage {

    private static final Log log = LogFactory.getLog(CodePage.class);

    //    private WebElement qrCode;
    private WebElement userInviteCode;
    private WebElement inviteButton;
    private WebElement explainButton;
//    private WebElement requestButton;

    public CodePage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("邀请好友");
//        webDriver.findElements(By.tagName("img")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(webElement -> "myShareQRCode".equals(webElement.getAttribute("name")))
//                .findFirst()
//                .ifPresent(element -> qrCode = element);

        webDriver
//                .findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().equals("邀请明细"))
                .findElements(By.id("toInviteList")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> inviteButton = element);

//        explainButton = webDriver.findElement(By.className("pbanner")).findElement(By.tagName("a"));

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(element -> element.getText().contains("合伙人"))
//                .findFirst()
//                .ifPresent(element -> requestButton = element);

//        assertThat(qrCode)
//                .isNotNull();
//        assertThat(qrCode.isDisplayed())
//                .isTrue();
        assertThat(userInviteCode)
                .isNotNull();
        assertThat(inviteButton)
                .isNotNull();
        assertThat(inviteButton.isDisplayed())
                .isTrue();
//        assertThat(qrCode)
//                .isNotNull();

    }

    /**
     * 确定是这个用户
     *
     * @param user
     * @param qrCodeService
     */
    public void assertUser(User user, QRCodeService qrCodeService) throws IOException {
        assertThat(getUserId())
                .isEqualToIgnoringCase(String.valueOf(user.getId()));
//        BufferedImage image = getQRCodeImage();
//        String url = qrCodeService.scanImage(image);
//        log.info("邀请url:" + url);
//        Long userId = CashFilter.guideUserFromURL(url, null);
//        assertThat(userId)
//                .isEqualTo(user.getId());

        assertThat(userInviteCode.getText())
                .endsWith(user.getInviteCode());

//        if (user.getAgentInfo() == null)
//            assertThat(requestButton)
//                    .isNotNull();
    }

    public String getUserId() {
        return webDriver.findElement(By.tagName("body")).getAttribute("name");
    }

//    public BufferedImage getQRCodeImage() throws IOException {
//        return toImage(qrCode);
//    }

    /**
     * 合伙人功能已取消
     *
     * @return
     */
    @Deprecated
    public AgentRequestPage requestAgent() {
//        requestButton.click();
//        throw new RuntimeException("这里没法支持申请合伙人。");
        explainButton.click();
        ExplainPage explainPage = initPage(ExplainPage.class);

        AgentRequestPage page = explainPage.requestAgent();
        // 这个页面还可以回到 ExplainPage
        page.toExplainPage();
        webDriver.navigate().back();
        return initPage(AgentRequestPage.class);
//        MyInvitationPage invitationPage = toMyInvitationPage();
//        invitationPage.assertNoTeam();
//
//        invitationPage.toRequestAgentPage();
    }

    public MyInvitationPage toMyInvitationPage() {
        inviteButton.click();
        return initPage(MyInvitationPage.class);
    }
}
