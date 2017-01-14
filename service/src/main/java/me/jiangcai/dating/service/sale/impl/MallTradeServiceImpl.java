package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.event.TradeDispatchRemindEvent;
import me.jiangcai.goods.lock.GoodsThreadSafe;
import me.jiangcai.goods.trade.Trade;
import me.jiangcai.goods.trade.TradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * @author CJ
 */
@Service("mallTradeService")
public class MallTradeServiceImpl implements MallTradeService {

    @Autowired
    private CashTradeRepository cashTradeRepository;
    @Autowired
    private OrderService orderService;

    @Override
    public CashTrade trade(long id) {
        return cashTradeRepository.getOne(id);
    }

    @Override
    @GoodsThreadSafe
    public PayOrder createPayOrder(long id, String payMethod) {
        CashTrade trade = trade(id);
        if (trade.getPayOrderSet() == null) {
            trade.setPayOrderSet(new HashSet<>());
        }
        return orderService.createPayOrder(trade, payMethod);
    }

    @Override
    public List<CashTrade> byOpenId(String openId) {
        return cashTradeRepository.findByUser_OpenId(openId);
    }

    @Override
    public void tradeDispatch(TradeDispatchRemindEvent event) {
        Trade trade = event.getTrade();
        if (trade instanceof TicketTrade) {
            TicketTrade ticketTrade = (TicketTrade) trade;
            ticketTrade.setStatus(TradeStatus.sent);
            // 发送发货通知
        }
    }
}
