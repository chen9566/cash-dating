package me.jiangcai.dating.service.sale;

import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.goods.event.TradeDispatchRemindEvent;
import me.jiangcai.goods.lock.GoodsThreadSafe;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商城订单系统
 *
 * @author CJ
 */
public interface MallTradeService {

    @Transactional(readOnly = true)
    CashTrade trade(long id);

    /**
     * 创建支付订单
     *
     * @param id        订单号
     * @param payMethod 支付方式，暂时只支持畅捷
     * @return
     */
    @GoodsThreadSafe
    @Transactional
    PayOrder createPayOrder(long id, String payMethod);

    /**
     * @param openId 用户openId
     * @return 所有订单
     */
    @Transactional(readOnly = true)
    List<CashTrade> byOpenId(String openId);

    @EventListener(TradeDispatchRemindEvent.class)
    @Transactional
    void tradeDispatch(TradeDispatchRemindEvent event);
}
