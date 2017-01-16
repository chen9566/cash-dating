package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.page.sale.SaleIndexPage;
import me.jiangcai.dating.page.sale.TicketGoodsDetailPage;
import me.jiangcai.dating.page.sale.TicketPayPage;
import me.jiangcai.dating.page.sale.TicketPaySuccessPage;
import me.jiangcai.dating.page.sale.TicketTradeSuccessPage;
import me.jiangcai.dating.repository.sale.TicketCodeRepository;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.trade.TradeStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class SaleControllerTest extends WebTest {
    @Autowired
    private MallTradeService mallTradeService;
    @Autowired
    private TicketCodeRepository ticketCodeRepository;

    @Test
    public void index() throws Exception {

        addSimpleTicketGoods();

        User user = helloNewUser(null, true);

        SaleIndexPage page = saleIndexPage();
//        page.printThisPage();

        final CashGoods ticketGoods = mallGoodsService.saleGoods().stream()
                .filter(CashGoods::isTicketGoods)
                .findAny()
                .orElse(null);
        TicketGoodsDetailPage detailPage = page.clickTicketGoods(ticketGoods);

        try {
            detailPage.buy(1);
            throw new AssertionError("还没有库存呢");
        } catch (Throwable ignored) {

        }

        mallGoodsService.addTicketBatch(user, (TicketGoods) ticketGoods, LocalDate.now().plusMonths(1)
                , UUID.randomUUID().toString(), Arrays.asList(randomMobile(), randomMobile()));

        TicketPayPage payPage = detailPage.buy(1);

        TicketPaySuccessPage successPage = payPage.toPay(null);

        //当前用户的唯一的订单
        CashTrade trade = mallTradeService.byOpenId(user.getOpenId()).get(0);
        assertThat(trade.getPayOrderSet())
                .isNotEmpty();
        System.out.println(trade);
//        successPage.printThisPage();
        // 订单应该已经发货
        assertThat(trade.getStatus())
                .isEqualByComparingTo(TradeStatus.sent);
        // 打开详情看看

        TicketTradeSuccessPage tradeSuccessPage = successPage.detail();
// 随便打开一个 应该是可以看到一个有效的二维码
        String qrCode = tradeSuccessPage.useRandomOne();

        TicketCode code = ticketCodeRepository.findByCode(qrCode);
        assertThat(code.isUserFlag())
                .isTrue();
// 刷新页面
        tradeSuccessPage.refresh();
        tradeSuccessPage.assertUsed(qrCode);
    }

}