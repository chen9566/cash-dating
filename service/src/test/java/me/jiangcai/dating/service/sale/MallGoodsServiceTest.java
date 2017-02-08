package me.jiangcai.dating.service.sale;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.sale.CashGoods;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mysql问题测试，需要回滚
 *
 * @author CJ
 */
@Transactional
public class MallGoodsServiceTest extends ServiceBaseTest {

    @Autowired
    private MallGoodsService mallGoodsService;

    @Test
    public void ticketInfo() throws Exception {
        try {
            CashGoods cashGoods = mallGoodsService.findGoods(1);
            System.out.println(mallGoodsService.ticketInfo(cashGoods));
        } catch (Throwable ignored) {
        }
    }

}