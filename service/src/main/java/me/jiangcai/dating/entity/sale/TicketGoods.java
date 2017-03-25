package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.util.Map;

/**
 * 卡券类商品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class TicketGoods extends CashGoods {

    /**
     * 购买须知，一段HTML
     */
    @Lob
    private String notes;

    @Override
    public boolean isTicketGoods() {
        return true;
    }

    @Id
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    protected void moreModel(Map<String, Object> data) {
        data.put("notes", notes);
    }

    @Transient
    @Override
    public Seller getSeller() {
        return null;
    }

    @Override
    public void setSeller(Seller seller) {

    }

    @Transient
    @Override
    public TradeEntity getOwner() {
        return null;
    }

    @Override
    public void setOwner(TradeEntity tradeEntity) {

    }
}
