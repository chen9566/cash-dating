package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.util.Map;

/**
 * 伪装的商品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class FakeGoods extends CashGoods {

    @ManyToOne
    private FakeCategory category;
    /**
     * 销量，可以随意修改
     */
    private long sales;
    /**
     * 库存，可以随意修改
     */
    private long stock;

    @Override
    public boolean isTicketGoods() {
        return false;
    }

    @Override
    protected void moreModel(Map<String, Object> data) {
        data.put("sales", sales);
        data.put("stock", stock);
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
}
