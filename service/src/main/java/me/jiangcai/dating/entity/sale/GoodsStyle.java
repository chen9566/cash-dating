package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.Goods;
import me.jiangcai.goods.GoodsImage;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.List;

/**
 * 规格
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
public class GoodsStyle implements Goods {

    @ManyToOne(optional = false)
    private CashGoods goods;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private boolean enable;

    @Override
    public Goods getReferenceGoods() {
        return goods;
    }

    @Override
    public List<? extends Goods> getAllReferencedGoods() {
        return null;
    }

    @Override
    public String getBrand() {
        return goods.getBrand();
    }

    @Override
    public Number getOriginalPrice() {
        return null;
    }

    @Override
    public Seller getSeller() {
        return null;
    }

    @Override
    public void setSeller(Seller seller) {

    }

    @Override
    public TradeEntity getOwner() {
        return null;
    }

    @Override
    public void setOwner(TradeEntity owner) {

    }

    @Override
    public List<? extends GoodsImage> getGoodsImages() {
        return null;
    }

    @Override
    public void addGoodsImage(GoodsImage goodsImage) {

    }

    @Override
    public String getStockStyle() {
        return null;
    }
}
