package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketBatch;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.entity.sale.TicketTradedGoods;
import me.jiangcai.dating.model.TicketInfo;
import me.jiangcai.dating.repository.sale.CashGoodsRepository;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.service.ManageGoodsService;
import me.jiangcai.goods.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * @author CJ
 */
@Service("mallGoodsService")
public class MallGoodsServiceImpl implements MallGoodsService {

    @Autowired
    private CashGoodsRepository cashGoodsRepository;
    @Autowired
    private CashTradeRepository cashTradeRepository;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private ManageGoodsService manageGoodsService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<CashGoods> saleGoods() {
        return cashGoodsRepository.findByEnableTrue();
    }

    @Override
    public TicketInfo ticketInfo(CashGoods goods) {
        // 获取所有批次 剩余可用数量 排除可用数量为0
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<TicketCode> ticketCodeRoot = criteriaQuery.from(TicketCode.class);
        Join<TicketBatch, TicketCode> ticketBatchJoin = ticketCodeRoot.join("batch", JoinType.LEFT);
        Path<TicketGoods> ticketGoods = ticketBatchJoin.get("goods");
//        Join<TicketGoods, TicketBatch> ticketGoodsJoin = ticketBatchJoin.join("goods", JoinType.LEFT);

        criteriaQuery = criteriaQuery.groupBy(ticketBatchJoin);
        criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(ticketBatchJoin.get("expiredDate")));
        Expression<Long> count = criteriaBuilder.count(ticketCodeRoot);

        criteriaQuery = criteriaQuery.multiselect(ticketBatchJoin.get("expiredDate"), count);
        criteriaQuery = criteriaQuery.where(criteriaBuilder.isFalse(ticketCodeRoot.get("used"))
                , criteriaBuilder.equal(ticketGoods, goods)
                , criteriaBuilder.greaterThan(count, 0L)
        );

        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
        try {
            Tuple tuple = query.setMaxResults(1).getSingleResult();
            return new TicketInfo(tuple.get(0, LocalDate.class), tuple.get(1, Long.class));
        } catch (NoResultException ex) {
            return new TicketInfo(LocalDate.now(), 0);
        }
    }

    @Override
    public Goods saveGoods(Goods goods) {
        return cashGoodsRepository.save((CashGoods) goods);
    }

    @Override
    public CashGoods findGoods(long id) {
        return cashGoodsRepository.findOne(id);
    }

    @Override
    public TicketGoods addTicketGoods(String stockStyle, String name, BigDecimal price, String subPrice
            , String description, String notes, String detail, String... imagePaths) throws IOException {
        TicketGoods ticketGoods = (TicketGoods) manageGoodsService.addGoods(TicketGoods::new
                , goods -> cashGoodsRepository.save((CashGoods) goods), null, null
                , name, price, imagePaths);

        ticketGoods.setSubPrice(subPrice);
        ticketGoods.setDescription(description);
        ticketGoods.setNotes(notes);
        ticketGoods.setRichDetail(detail);
        return ticketGoods;
    }

    @Override
    public CashTrade createOrder(User user, CashGoods goods, int count) {
        return (CashTrade) tradeService.createTrade(() -> {
                    if (goods.isTicketGoods()) return new TicketTrade();
                    throw new IllegalStateException("暂时不支持" + goods);
                }, trade -> cashTradeRepository.saveAndFlush((CashTrade) trade)
                , trade -> cashTradeRepository.getOne(((CashTrade) trade).getId())
                , (goods1, token) -> {
                    if (goods1 instanceof TicketGoods) {
                        TicketTradedGoods tradedGoods = new TicketTradedGoods();
                        tradedGoods.setTicketCode((TicketCode) token);
                        return tradedGoods;
                    }
                    throw new IllegalStateException("暂时不支持" + goods1);
                }
                , goods, count, user, Duration.ofMinutes(10)
        );
    }
}
