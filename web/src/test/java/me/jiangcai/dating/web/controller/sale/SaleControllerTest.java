package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.page.sale.SaleIndexPage;
import me.jiangcai.dating.page.sale.TicketGoodsDetailPage;
import me.jiangcai.dating.page.sale.TicketPayPage;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.goods.service.ManageGoodsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author CJ
 */
public class SaleControllerTest extends WebTest {
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private ManageGoodsService manageGoodsService;

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
        payPage.printThisPage();
    }

    private void addSimpleTicketGoods() throws IOException {
        if (mallGoodsService.saleGoods().stream().noneMatch(CashGoods::isTicketGoods)) {
            int imagesCount = 1 + random.nextInt(3);
            String[] images = new String[imagesCount];
            for (int i = 0; i < images.length; i++) {
                images[i] = randomImageResourcePath();
            }

            TicketGoods goods = mallGoodsService.addTicketGoods("T1", "星巴克8折优惠券", randomOrderAmount()
                    , "¥108", "绝对低价，超值享受", "", ""
                    , images);

            manageGoodsService.enableGoods(goods, mallGoodsService::saveGoods);
        }
    }

}