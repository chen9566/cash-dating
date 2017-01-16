package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.entity.sale.TicketTradedGoods;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.event.TradeDispatchRemindEvent;
import me.jiangcai.goods.lock.GoodsThreadSafe;
import me.jiangcai.goods.trade.Trade;
import me.jiangcai.goods.trade.TradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

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

    @Override
    public TicketCode ticketCode(String code, User user) {
        final TypedQuery<TicketCode> query = userTicketQuery(code, user);
        // NoResultException
        return query.getSingleResult();
    }

    private TypedQuery<TicketCode> userTicketQuery(String code, User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketCode> ticketCodeCriteriaQuery = criteriaBuilder.createQuery(TicketCode.class);

        Root<TicketTrade> tradeRoot = ticketCodeCriteriaQuery.from(TicketTrade.class);
        Join<TicketTrade, TicketTradedGoods> tradedGoodsTicketTradeJoin = tradeRoot.joinSet("tradedSet");
        Join<TicketTradedGoods, TicketCode> ticketCodeTicketTradedGoodsJoin = tradedGoodsTicketTradeJoin.joinSet("codeSet");

        //查询条件
        Predicate predicate = criteriaBuilder.and(criteriaBuilder.equal(tradeRoot.get("user"), user)
                , tradeRoot.get("status").in(TradeStatus.confirmed, TradeStatus.sent)
                , criteriaBuilder.isTrue(tradeRoot.get("paidSuccess")));
        if (code == null)
            ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.where(predicate);
        else
            ticketCodeCriteriaQuery = ticketCodeCriteriaQuery
                    .where(criteriaBuilder.equal(ticketCodeTicketTradedGoodsJoin.get("code"), code), predicate);

        ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.distinct(true);
        ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.select(ticketCodeTicketTradedGoodsJoin);

        return entityManager.createQuery(ticketCodeCriteriaQuery);
    }

    @Override
    public List<TicketCode> ticketCodes(User user) {
        return userTicketQuery(null, user).getResultList();
    }
}
