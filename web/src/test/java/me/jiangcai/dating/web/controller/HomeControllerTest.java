package me.jiangcai.dating.web.controller;

import com.google.common.base.Predicate;
import me.jiangcai.chanpay.test.mock.MockPay;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.page.MyBankPage;
import me.jiangcai.dating.page.MyDataPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.PayCompletedPage;
import me.jiangcai.dating.page.ShowOrderPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

//        page.clickMenu("我要收款");
//        initPage(StartOrderPage.class);
        driver.get("http://localhost/my");
        page.reloadPageInfo();

        page.clickMenu("我的银行卡");
        bank(initPage(MyBankPage.class));
        driver.get("http://localhost/my");
        page.reloadPageInfo();

        myData(page.clickMyData());

    }

    private void myData(MyDataPage dataPage) {
        dataPage.assertUser(currentUser());
    }


    private void bank(MyBankPage page) {
        page.assertCard(currentUser().getCards().stream().filter(card -> !card.isDisabled()).collect(Collectors.toList()));
    }

    @Test
    public void index() throws Exception {
        StartOrderPage page = startOrderPage();

        // 小数点前 只可以有7位
        // 最大 9,999,999.99
        double amount = (double) Math.abs(random.nextInt(9999999)) + Math.abs(random.nextDouble());
        // 2109694666.00
        //  856846127.38
        //   76352258.18&version=1.0
        //    6491427.62 work

//        page.
        ShowOrderPage codePage = page.pay(amount, UUID.randomUUID().toString(), null);

        // 这个时候应该是到了二维码界面,在这个界面 我们可以分享它
        codePage.assertAmount(amount);

        PayMethod channel = PayMethod.values()[random.nextInt(PayMethod.values().length)];
        codePage.pay(channel);

        List<CashOrder> orderList = currentOrders();
        assertThat(orderList)
                .isNotEmpty()
                .hasSize(1);

        CashOrder order = orderList.get(0);
        assertThat(order.getPlatformOrderSet())
                .isNotEmpty()
                .hasSize(1);

        new WebDriverWait(driver, 5)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(@Nullable WebDriver input) {
                        if (input == null)
                            return false;
                        return input.getTitle().equals("付款成功");
                    }
                });
        //
        PayCompletedPage payCompletedPage = initPage(PayCompletedPage.class);
        // 刚打款
        payCompletedPage.assertJustPayWithoutWithdrawal();
        payCompletedPage.assertVisitByMyself();
        //
//        Thread.sleep(2000);
//
//        // 同时 我们的订单应该也是牛逼了!
//        driver.get(orderUrl);
//        currentPageIsCompleted();
    }

}