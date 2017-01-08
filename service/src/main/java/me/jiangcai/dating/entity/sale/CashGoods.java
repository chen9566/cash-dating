package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.core.entity.Goods;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

/**
 * 款爷商品，那肯定都是打折的
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CashGoods extends Goods {

    /**
     * 副价格，一般显示加上删除线
     */
    @Column(length = 30)
    private String subPrice;

    /**
     * @return 是否卡券类
     */
    @Transient
    public abstract boolean isTicketGoods();

    @Id
    @Override
    public Long getId() {
        return super.getId();
    }
}
