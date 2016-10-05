package me.jiangcai.dating.web.controller;

import me.jiangcai.chanpay.test.mock.MockPay;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.page.QRCodePage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.QRCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 已登录的
 *
 * @author CJ
 */
public class HomeControllerTest extends LoginWebTest {

    private static final Log log = LogFactory.getLog(HomeControllerTest.class);

    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private OrderService orderService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MockPay pay;

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/");

        StartOrderPage page = initPage(StartOrderPage.class);

        int amount = Math.abs(random.nextInt());

        page.pay(amount, "");

        // 这个时候应该是到了二维码界面,在这个界面 我们可以分享它
        QRCodePage codePage = initPage(QRCodePage.class);
        // 并且拥有了一个新订单

        // TODO 微信分享  分享的时候 应该携带者邀请信息
        // 我们长按这个二维码进行
        BufferedImage image = codePage.scanCode();

        String url = qrCodeService.scanImage(image);

        log.info("the url scanned:" + url);

        driver.get(url);
//        System.out.println(driver.getPageSource());
        // 如果打开这个支付页面应该是可以完成支付的
        // 现在的目标是获得这个页面url
        String chanpayUrl = driver.findElement(By.id("platformFrame")).getAttribute("src");

        List<Order> orderList = orderService.findOrders(detail.getOpenId());
        assertThat(orderList)
                .isNotEmpty()
                .hasSize(1);

        Order order = orderList.get(0);
        assertThat(order.getPlatformOrderSet())
                .isNotEmpty()
                .hasSize(1);

        pay.pay(order.getPlatformOrderSet().iterator().next().getId(), chanpayUrl);

        Thread.sleep(1000);

        // 同时 我们的订单应该也是牛逼了!
        driver.get(url);
        currentPageIsCompleted();
    }

    /**
     * 当前页面显示的是 订单已完成
     */
    private void currentPageIsCompleted() {
        assertThat(driver.getTitle())
                .isEqualTo("订单已完成");
    }

}