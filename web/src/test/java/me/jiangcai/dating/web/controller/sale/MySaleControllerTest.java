package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.sale.MySalePage;
import me.jiangcai.dating.page.sale.OrderListPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MySaleControllerTest extends WebTest {

    @Test
    public void index() throws Exception {
        // 购买的业务
        User user = helloNewUser(null, true);

        // 下单数个，付款数个，确认数个
        final int ordered = random.nextInt(5) + 1;
        final int paid = random.nextInt(5) + 1;
        final int sures = random.nextInt(5) + 1;
        makeTicketTrade(user, ordered, paid, sures);

        MySalePage myPage = mySalePage();

        myPage.assertUsableTicket(paid + sures);
        myPage.assertUsedTicket(0);

        myPage.clickUsableOneAndUseIt();
        myPage.refresh();
        myPage.assertUsableTicket(paid + sures - 1);
        myPage.assertUsedTicket(1);
//        myPage.printThisPage();
        myPage.clickUsedOneAndBack();


        OrderListPage allOrdersPage = myPage.allOrders();
        // 把订单数量给弄出来
        assertThat(allOrdersPage.count())
                .isEqualTo(ordered + paid + sures);

        driver.navigate().back();
        myPage.refresh();

        //
        OrderListPage waitingPayOrdersPage = myPage.waitingPayOrders();
        assertThat(waitingPayOrdersPage.count())
                .isEqualTo(ordered);

        driver.navigate().back();
        myPage.refresh();

        //
        OrderListPage waitingSendOrdersPage = myPage.waitingSendOrders();
        assertThat(waitingSendOrdersPage.count())
                .isEqualTo(0);

        driver.navigate().back();
        myPage.refresh();

        //
        OrderListPage waitingReceiveOrdersPage = myPage.waitingReceiveOrders();
        assertThat(waitingReceiveOrdersPage.count())
                .isEqualTo(paid);

        driver.navigate().back();
        myPage.refresh();

        //走一下各种流程
        //我们得想办法弄到一个关闭的订单
        allOrdersPage = myPage.allOrders();
        // 所有订单状态都查看一次详情
        allOrdersPage.openAllStatus();


    }

}