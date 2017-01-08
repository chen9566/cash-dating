package me.jiangcai.dating.entity.sale;

import me.jiangcai.dating.entity.User;
import me.jiangcai.goods.Buyer;
import me.jiangcai.goods.core.entity.Trade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 * @author CJ
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CashTrade extends Trade {

    /**
     * 购买者
     */
    @ManyToOne(optional = false)
    private User user;

    @Id
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setBuyer(Buyer buyer) {
        user = (User) buyer;
    }

}
