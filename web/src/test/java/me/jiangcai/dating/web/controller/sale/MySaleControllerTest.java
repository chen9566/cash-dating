package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.sale.MySalePage;
import me.jiangcai.dating.page.sale.OrderListPage;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.trade.TradeStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MySaleControllerTest extends WebTest {

    @Autowired
    private MallTradeService mallTradeService;
    @Autowired
    private CashTradeRepository cashTradeRepository;

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

//        myPage.printThisPage();

        myPage.assertUsableTicket(paid + sures);
        myPage.assertUsedTicket(0);

        OrderListPage allOrdersPage = myPage.allOrders();
        // 把订单数量给弄出来
        assertThat(allOrdersPage.count())
                .isEqualTo(ordered + paid + sures);
        // 对于目前出现的一个问题 在此添加一个测试

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

        // 检查等待确认收货
        OrderListPage waitingReceiveOrdersPage = myPage.waitingReceiveOrders();
        assertThat(waitingReceiveOrdersPage.count())
                .isEqualTo(paid);

//        driver.navigate().back();
//        myPage.refresh();

        myPage = mySalePage();

        // 用了一个
        myPage.clickUsableOneAndUseIt();
        myPage.refresh();
        myPage.assertUsableTicket(paid + sures - 1);
        myPage.assertUsedTicket(1);
//        myPage.printThisPage();
        myPage.clickUsedOneAndBack();

        myPage.refresh();

        // 这个时候应该自动确认了一个
        waitingReceiveOrdersPage = myPage.waitingReceiveOrders();
        assertThat(waitingReceiveOrdersPage.count())
                .isGreaterThanOrEqualTo(paid - 1)
                .isLessThanOrEqualTo(paid);

        //走一下各种流程
        //我们得想办法弄到一个关闭的订单
        cashTradeRepository.findAll(mallTradeService.tradeSpecification(user, TradeStatus.ordered)).stream()
                .findAny().ifPresent(trade -> {
            mallTradeService.closeTrade(trade.getId());
        });

        myPage = mySalePage();

        allOrdersPage = myPage.allOrders();
        // 所有订单状态都查看一次详情
        allOrdersPage.openAllStatus();

    }

}