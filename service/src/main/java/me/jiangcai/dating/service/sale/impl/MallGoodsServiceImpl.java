package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.FakeTrade;
import me.jiangcai.dating.entity.sale.TicketBatch;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.entity.sale.TicketTradedGoods;
import me.jiangcai.dating.entity.sale.support.FakeTradedGoods;
import me.jiangcai.dating.model.TicketInfo;
import me.jiangcai.dating.repository.sale.CashGoodsRepository;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.repository.sale.TicketBatchRepository;
import me.jiangcai.dating.repository.sale.TicketCodeRepository;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.service.ManageGoodsService;
import me.jiangcai.goods.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

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
    @Autowired
    private TicketBatchRepository ticketBatchRepository;
    @Autowired
    private TicketCodeRepository ticketCodeRepository;

    @Override
    public List<CashGoods> saleGoods() {
        return cashGoodsRepository.findByEnableTrue();
    }

    @Override
    public TicketInfo ticketInfo(CashGoods goods) {
        // 获取所有批次 剩余可用数量 排除可用数量为0
        Query query = entityManager.createQuery("select batch.expiredDate,count(code) from TicketCode  as code join code.batch as batch where code.used=false and batch.goods=:goods group by batch order by batch.expiredDate asc");
        query.setParameter("goods", goods);
        try {
            Object[] objects = (Object[]) query.setMaxResults(1).getSingleResult();
            return new TicketInfo((LocalDate) objects[0], ((Number) objects[1]).longValue());
        } catch (NoResultException ex) {
            return new TicketInfo(LocalDate.now(), 0);
        }
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
//        Root<TicketCode> ticketCodeRoot = criteriaQuery.from(TicketCode.class);
//        Join<TicketBatch, TicketCode> ticketBatchJoin = ticketCodeRoot.join("batch", JoinType.LEFT);
//        Path<TicketGoods> ticketGoods = ticketBatchJoin.get("goods");
////        Join<TicketGoods, TicketBatch> ticketGoodsJoin = ticketBatchJoin.join("goods", JoinType.LEFT);
//
//        criteriaQuery = criteriaQuery.groupBy(ticketBatchJoin);
//        criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(ticketBatchJoin.get("expiredDate")));
//        Expression<Long> count = criteriaBuilder.count(ticketCodeRoot);
//
//        criteriaQuery = criteriaQuery.multiselect(ticketBatchJoin.get("expiredDate"), count);
//        criteriaQuery = criteriaQuery.where(criteriaBuilder.isFalse(ticketCodeRoot.get("used"))
//                , criteriaBuilder.equal(ticketGoods, goods)
//                , criteriaBuilder.greaterThan(count, 0L)
//        );
//
//        TypedQuery<Tuple> query = entityManager.createQuery(criteriaQuery);
//        try {
//            Tuple tuple = query.setMaxResults(1).getSingleResult();
//            return new TicketInfo(tuple.get(0, LocalDate.class), tuple.get(1, Long.class));
//        } catch (NoResultException ex) {
//            return new TicketInfo(LocalDate.now(), 0);
//        }
    }

    @Override
    public Goods saveGoods(Goods goods) {
        final CashGoods cashGoods = (CashGoods) goods;
        cashGoods.setUpdateTime(LocalDateTime.now());
        return cashGoodsRepository.save(cashGoods);
    }

    @Override
    public CashGoods findGoods(long id) {
        return cashGoodsRepository.findOne(id);
    }

    @Override
    public TicketGoods addTicketGoods(String brand, String stockStyle, String name, BigDecimal price, String subPrice
            , String description, String notes, String detail, String... imagePaths) throws IOException {
        TicketGoods ticketGoods = (TicketGoods) manageGoodsService.addGoods(TicketGoods::new
                , goods -> cashGoodsRepository.save((CashGoods) goods), null, null
                , name, price, imagePaths);
        ticketGoods.setCreateTime(LocalDateTime.now());
        ticketGoods.setBrand(brand);
        ticketGoods.setSubPrice(subPrice);
        ticketGoods.setDescription(description);
        ticketGoods.setNotes(notes);
        ticketGoods.setRichDetail(detail);
        return ticketGoods;
    }

    @Override
    public TicketGoods addTicketGoods(String name, String price, String stockStyle) throws IOException {
        return addTicketGoods(null, stockStyle, name, new BigDecimal(price), null, null, null, null);
    }

    @Override
    public FakeGoods addFakeGoods(String name, String price) throws IOException {
        FakeGoods fakeGoods = (FakeGoods) manageGoodsService.addGoods(FakeGoods::new
                , goods -> cashGoodsRepository.save((CashGoods) goods), null, null
                , name, new BigDecimal(price));
        fakeGoods.setCreateTime(LocalDateTime.now());
        return fakeGoods;
    }

    @Override
    public CashTrade createOrder(User user, CashGoods goods, int count) {
        return (CashTrade) tradeService.createTrade(() -> {
                    if (goods.isTicketGoods()) return new TicketTrade();
                    if (goods instanceof FakeGoods)
                        return new FakeTrade();
                    throw new IllegalStateException("暂时不支持" + goods);
                }, trade -> cashTradeRepository.saveAndFlush((CashTrade) trade)
                , trade -> cashTradeRepository.getOne(((CashTrade) trade).getId())
                , (goods1, token) -> {
                    // TODO 一个商品 应该可以保存多个数据
                    //  应该通过调整这个方法 让这个方法允许接受多个Token 当然goods1得是一样的
                    if (goods1 instanceof TicketGoods) {
                        TicketTradedGoods tradedGoods = new TicketTradedGoods();
                        tradedGoods.setCodeSet(new HashSet<>());
                        Stream.of(token).forEach(t -> tradedGoods.getCodeSet().add((TicketCode) t));
                        return tradedGoods;
                    }
                    if (goods1 instanceof FakeGoods) {
                        return new FakeTradedGoods((FakeGoods) goods1, token.length);
                    }
                    throw new IllegalStateException("暂时不支持" + goods1);
                }
                , goods, count, user, Duration.ofMinutes(10)
        );
    }

    @Override
    public TicketBatch addTicketBatch(User user, TicketGoods goods, LocalDate expiredDate, String comment
            , Iterable<String> codes) {
        TicketBatch batch = new TicketBatch();
        batch.setComment(comment);
        batch.setCreatedTime(LocalDateTime.now());
        batch.setCreator(user);
        batch.setExpiredDate(expiredDate);
        batch.setGoods(goods);

        batch = ticketBatchRepository.save(batch);
        for (String code : codes) {
            TicketCode ticketCode = new TicketCode();
            ticketCode.setBatch(batch);
            ticketCode.setCode(code);
            ticketCodeRepository.save(ticketCode);
        }
        return batch;
    }
}
