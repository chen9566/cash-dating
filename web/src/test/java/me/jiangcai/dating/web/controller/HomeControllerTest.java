package me.jiangcai.dating.web.controller;

import me.jiangcai.chanpay.test.mock.MockPay;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.page.MyBankPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.ShowOrderPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

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
    private StatisticService statisticService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MockPay pay;

    @Test
    public void my() {
        // 我的
        driver.get("http://localhost/my");
        MyPage page = initPage(MyPage.class);
//        System.out.println(page);

        page.assertFrom(currentUser(), statisticService);

        page.clickMenu("我要收款");
        initPage(StartOrderPage.class);
        driver.get("http://localhost/my");
        page.reloadPageInfo();

        page.clickMenu("我的银行卡");
        bank(initPage(MyBankPage.class));
        driver.get("http://localhost/my");
        page.reloadPageInfo();

    }


    private void bank(MyBankPage page) {
        page.assertCard(currentUser().getCards());
    }

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/start");

        StartOrderPage page = initPage(StartOrderPage.class);

        // 小数点前 只可以有7位
        // 最大 9,999,999.99
        double amount = (double) Math.abs(random.nextInt(9999999)) + Math.abs(random.nextDouble());
        // 2109694666.00
        //  856846127.38
        //   76352258.18&version=1.0
        //    6491427.62 work

        page.pay(amount, UUID.randomUUID().toString());

        // 这个时候应该是到了二维码界面,在这个界面 我们可以分享它
        ShowOrderPage codePage = initPage(ShowOrderPage.class);
        // 并且拥有了一个新订单
        String orderUrl = driver.getCurrentUrl();

        // TODO 微信分享  分享的时候 应该携带者邀请信息
        // 我们长按这个二维码进行
        BufferedImage image = codePage.scanCode();

        String url = qrCodeService.scanImage(image);

        log.info("the url scanned:" + url);


//        driver.get(url);
//        System.out.println(driver.getPageSource());
        // 如果打开这个支付页面应该是可以完成支付的
        // 现在的目标是获得这个页面url
//        String chanpayUrl = driver.findElement(By.id("platformFrame")).getAttribute("src");

        List<CashOrder> orderList = currentOrders();
        assertThat(orderList)
                .isNotEmpty()
                .hasSize(1);

        CashOrder order = orderList.get(0);
        assertThat(order.getPlatformOrderSet())
                .isNotEmpty()
                .hasSize(1);

        pay.pay(order.getPlatformOrderSet().iterator().next().getId(), url);

        Thread.sleep(2000);

        // 同时 我们的订单应该也是牛逼了!
        driver.get(orderUrl);
        currentPageIsCompleted();
    }

    /**
     * 当前页面显示的是 订单已完成
     */
    private void currentPageIsCompleted() {
//        System.out.println(driver.getPageSource());
        assertThat(driver.getTitle())
                .isEqualTo("订单已完成");
    }

}