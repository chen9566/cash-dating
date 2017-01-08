package me.jiangcai.dating.entity.sale;

import me.jiangcai.goods.core.entity.Trade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * @author CJ
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CashTrade extends Trade {

    @Id
    @Override
    public Long getId() {
        return super.getId();
    }
}
