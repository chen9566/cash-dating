package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.exception.ShortageStockException;
import me.jiangcai.goods.stock.StockService;
import me.jiangcai.goods.stock.StockToken;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author CJ
 */
@Service
public class FakeStockService implements StockService {
    @Override
    public boolean support(Goods goods) {
        goods = goods.getFinalReferenceGood();
        CashGoods cashGoods = (CashGoods) goods;
        return cashGoods instanceof FakeGoods;
    }

    @Override
    public Collection<? extends StockToken> lock(Goods goods, int count) throws ShortageStockException {
        FakeGoods fakeGoods = (FakeGoods) goods;
        fakeGoods.setSales(fakeGoods.getSales() + count);
        fakeGoods.setStock(fakeGoods.getStock() - count);
        StockToken[] buffer = new StockToken[count];
        return Arrays.asList(buffer);
    }

    @Override
    public void release(Goods goods, StockToken token) {
        FakeGoods fakeGoods = (FakeGoods) goods;
        fakeGoods.setSales(fakeGoods.getSales() - 1);
        fakeGoods.setStock(fakeGoods.getStock() + 1);
    }

    @Override
    public long usableCount(Goods goods) {
        FakeGoods fakeGoods = (FakeGoods) goods;
        return fakeGoods.getStock();
    }
}
