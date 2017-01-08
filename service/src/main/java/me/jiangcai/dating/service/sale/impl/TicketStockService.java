package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.dating.repository.sale.TicketCodeRepository;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.exception.ShortageStockException;
import me.jiangcai.goods.stock.StockService;
import me.jiangcai.goods.stock.StockToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class TicketStockService implements StockService {

    @Autowired
    private TicketCodeRepository ticketCodeRepository;

    @Override
    public boolean support(Goods goods) {
        goods = goods.getFinalReferenceGood();
        CashGoods cashGoods = (CashGoods) goods;
        return cashGoods.isTicketGoods();
    }

    @Override
    public Collection<? extends StockToken> lock(Goods goods, int count) throws ShortageStockException {
        List<TicketCode> codeList = ticketCodeRepository.findAll((root, query, cb) -> cb.and(
                cb.equal(root.get("batch").get("goods"), goods)
                , cb.isFalse(root.get("used"))
        ), new PageRequest(0, count)).getContent();

        if (codeList.size() < count)
            throw new ShortageStockException();

        codeList.forEach(goodsCode -> {
            goodsCode.setUsed(true);
            goodsCode.setUsedTime(LocalDateTime.now());
        });

        return codeList;
    }

    @Override
    public void release(Goods goods, StockToken token) {
        ticketCodeRepository.getOne(new TicketCodePK(goods.getId(), token.productSKUCode())).setUsed(false);
    }

    @Override
    public long usableCount(Goods goods) {
        return ticketCodeRepository.countByBatch_GoodsAndUsedFalse((TicketGoods) goods);
    }
}
