package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.QRCodePage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.QRCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 已登录的
 *
 * @author CJ
 */
public class HomeControllerTest extends LoginWebTest {

    private static final Log log = LogFactory.getLog(HomeControllerTest.class);

    @Autowired
    private QRCodeService qrCodeService;

    @Test
    public void index() throws IOException {
        driver.get("http://localhost/");

        StartOrderPage page = initPage(StartOrderPage.class);

        int amount = Math.abs(random.nextInt());

        page.pay(amount, "");

        // 这个时候应该是到了二维码界面,在这个界面 我们可以分享它
        QRCodePage codePage = initPage(QRCodePage.class);
        // 并且拥有了一个新订单

        // TODO 微信分享
        // 我们长按这个二维码进行
        BufferedImage image = codePage.scanCode();

        String url = qrCodeService.scanImage(image);

        log.info("the url scanned:" + url);

//        driver.get(url);
//        System.out.println(driver.getPageSource());
    }

}