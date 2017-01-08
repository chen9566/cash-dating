package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.GoodsImage;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.stock.StockToken;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author CJ
 */
@Embeddable
@Setter
@Getter
public class TicketTradedGoods implements TradedGoods {

    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private TicketCode ticketCode;

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public StockToken toStockToken() {
        return ticketCode;
    }

    private Goods myGoods() {
        return ticketCode.getBatch().getGoods();
    }

    @Override
    public Goods getReferenceGoods() {
        return myGoods().getReferenceGoods();
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
