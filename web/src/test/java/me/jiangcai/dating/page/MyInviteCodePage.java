package me.jiangcai.dating.page;

import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.QRCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MyInviteCodePage extends AbstractPage {

    private static final Log log = LogFactory.getLog(MyInviteCodePage.class);

    private WebElement qrCode;
    private WebElement message;
    private WebElement inviteButton;
    private WebElement requestButton;

    public MyInviteCodePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.tagName("img")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> qrCode = element);

        webDriver.findElements(By.tagName("h4")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(element -> message = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().equals("邀请"))
                .findFirst()
                .ifPresent(element -> inviteButton = element);

        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> element.getText().contains("代理"))
                .findFirst()
                .ifPresent(element -> requestButton = element);

        assertThat(qrCode)
                .isNotNull();
        assertThat(qrCode.isDisplayed())
                .isTrue();
        assertThat(message)
                .isNotNull();
        assertThat(inviteButton)
                .isNotNull();
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
        BufferedImage image = getQRCodeImage();
        String url = qrCodeService.scanImage(image);
        log.info("邀请url:" + url);
        Long userId = CashFilter.guideUserFromURL(url, null);
        assertThat(userId)
                .isEqualTo(user.getId());

        assertThat(message.getText())
                .endsWith(user.getInviteCode());

        if (user.getAgentInfo() == null)
            assertThat(requestButton)
                    .isNotNull();
    }

    public BufferedImage getQRCodeImage() throws IOException {
        return toImage(qrCode);
    }

    public void requestAgent() {
        requestButton.click();
    }
}
