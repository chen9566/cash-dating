package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.GoodsImage;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.stock.StockToken;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class TicketTradedGoods implements TradedGoods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 一个预留字段
     */
    private int status;

//    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
//    private TicketCode ticketCode;
    /**
     * 里面的批次可以不一样，但商品必须是一样的
     */
    @OneToMany
    private Set<TicketCode> codeSet;

    @Override
    public int getCount() {
        return codeSet.size();
    }

    @Override
    public StockToken[] toStockToken() {
        return codeSet.toArray(new StockToken[codeSet.size()]);
    }

    private Goods myGoods() {
        return codeSet.stream().findAny().orElseThrow(() -> new IllegalStateException("至少得有一个商品的"))
                .getBatch().getGoods();
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
