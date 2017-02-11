package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.entity.sale.TicketTradedGoods;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.event.TradeDispatchRemindEvent;
import me.jiangcai.goods.lock.GoodsThreadSafe;
import me.jiangcai.goods.service.TradeService;
import me.jiangcai.goods.trade.Trade;
import me.jiangcai.goods.trade.TradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    @Autowired
    private TradeService tradeService;

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
    public TicketCode ticketCode(TicketCodePK code, User user) {
        final TypedQuery<TicketCode> query = userTicketQuery(code, user);
        // NoResultException
        return query.getSingleResult();
    }

    private TypedQuery<TicketCode> userTicketQuery(TicketCodePK code, User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketCode> ticketCodeCriteriaQuery = criteriaBuilder.createQuery(TicketCode.class);

        Root<TicketTrade> tradeRoot = ticketCodeCriteriaQuery.from(TicketTrade.class);
        Join<TicketTrade, TicketTradedGoods> tradedGoodsTicketTradeJoin = tradeRoot.joinSet("tradedSet");
        Join<TicketTradedGoods, TicketCode> ticketCodeTicketTradedGoodsJoin = tradedGoodsTicketTradeJoin.joinSet("codeSet");

        //查询条件
        Predicate predicate = criteriaBuilder.and(CashTrade.belongUser(user, criteriaBuilder, tradeRoot)
                , tradeRoot.get("status").in(TradeStatus.confirmed, TradeStatus.sent)
                , criteriaBuilder.isTrue(tradeRoot.get("paidSuccess")));
        if (code == null)
            ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.where(predicate);
        else
            ticketCodeCriteriaQuery = ticketCodeCriteriaQuery
                    .where(
                            criteriaBuilder.equal(ticketCodeTicketTradedGoodsJoin.get("code"), code.getCode())
                            , criteriaBuilder.equal(ticketCodeTicketTradedGoodsJoin.get("batch").get("id"), code.getBatch())
                            , predicate);

        ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.distinct(true);
        ticketCodeCriteriaQuery = ticketCodeCriteriaQuery.select(ticketCodeTicketTradedGoodsJoin);

        return entityManager.createQuery(ticketCodeCriteriaQuery);
    }

    @Override
    public List<TicketCode> ticketCodes(User user) {
        return userTicketQuery(null, user).getResultList();
    }

    @Override
    public Specification<CashTrade> tradeSpecification(User user, TradeStatus type) {
        // 只处理这么几个状态
        return (root, query, cb) -> {
            Predicate belong = CashTrade.belongUser(user, cb, root);
            if (type != null)
                return cb.and(belong, cb.equal(root.get("status"), type));
            // 只受理这么几个状态
            return cb.and(belong, root.get("status").in(TradeStatus.closed, TradeStatus.ordered, TradeStatus.paid, TradeStatus.sent
                    , TradeStatus.confirmed));
        };
    }

    @Override
    public void confirmTrade(User user, long id) {
        CashTrade trade = cashTradeRepository.getOne(id);
        if (!trade.getUser().equals(user))
            throw new AccessDeniedException("");
        if (trade.getStatus() != TradeStatus.sent)
            throw new IllegalStateException();
        trade.setStatus(TradeStatus.confirmed);
    }

    @Override
    public void closeTrade(long id) {
        CashTrade trade = trade(id);
        trade.setCloseTime(LocalDateTime.now().minusMonths(1));
        tradeService.checkTrade(trade, Function.identity());
    }

    @Override
    public Map<TradeStatus, Number> tradeCounts(User user) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<CashTrade> root = query.from(CashTrade.class);

        final Path<Object> statusPath = root.get("status");
        query = query.groupBy(statusPath);
        query = query.multiselect(statusPath, criteriaBuilder.count(root));
        query = query.where(criteriaBuilder.equal(root.get("user"), user));

        TypedQuery<Tuple> tupleQuery = entityManager.createQuery(query);
        Map<TradeStatus, Number> counts = new HashMap<>();
        tupleQuery.getResultList().forEach(tuple -> {
            TradeStatus status = tuple.get(0, TradeStatus.class);
            Number count = (Number) tuple.get(1);
            counts.put(status, count);
        });
        return counts;
    }
}
