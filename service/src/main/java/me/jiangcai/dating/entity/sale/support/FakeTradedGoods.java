package me.jiangcai.dating.entity.sale.support;

import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.GoodsImage;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.stock.StockToken;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author CJ
 */
public class FakeTradedGoods implements TradedGoods {

    private FakeGoods goods;
    private int count;

    public FakeTradedGoods(FakeGoods goods, int count) {
        this.goods = goods;
        this.count = count;
    }

    public FakeGoods getGoods() {
        return goods;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public StockToken[] toStockToken() {
        return new StockToken[count];
    }

    private Goods myGoods() {
        return goods;
    }


    @Override
    public Goods getReferenceGoods() {
        final Goods myGoods = myGoods();
        Goods goods = myGoods.getReferenceGoods();
        if (goods != null)
            return goods;
        return myGoods;
    }

    @Override
    public List<? extends Goods> getAllReferencedGoods() {
        return myGoods().getAllReferencedGoods();
    }

    @Override
    public Long getId() {
        return myGoods().getId();
    }

    @Override
    public String getName() {
        return myGoods().getName();
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getBrand() {
        return myGoods().getBrand();
    }

    @Override
    public Number getPrice() {
        return myGoods().getPrice();
    }

    @Override
    public void setPrice(BigDecimal price) {

    }

    @Override
    public Number getOriginalPrice() {
        return myGoods().getOriginalPrice();
    }

    @Override
    public Seller getSeller() {
        return myGoods().getSeller();
    }

    @Override
    public void setSeller(Seller seller) {

    }

    @Override
    public TradeEntity getOwner() {
        return myGoods().getOwner();
    }

    @Override
    public void setOwner(TradeEntity owner) {

    }

    @Override
    public List<? extends GoodsImage> getGoodsImages() {
        return myGoods().getGoodsImages();
    }

    @Override
    public void addGoodsImage(GoodsImage goodsImage) {

    }

    @Override
    public boolean isEnable() {
        return myGoods().isEnable();
    }

    @Override
    public void setEnable(boolean enable) {

    }

    @Override
    public String getStockStyle() {
        return myGoods().getStockStyle();
    }
}
