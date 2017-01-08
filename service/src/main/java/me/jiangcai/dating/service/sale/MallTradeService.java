package me.jiangcai.dating.service.sale;

import me.jiangcai.dating.entity.sale.CashTrade;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商城订单系统
 *
 * @author CJ
 */
public interface MallTradeService {

    @Transactional(readOnly = true)
    CashTrade trade(long id);
}
