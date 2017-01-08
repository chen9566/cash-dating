package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.core.entity.Goods;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

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
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "goods")
    private List<GoodsStyle> styleList;

    /**
     * @return 是否卡券类
     */
    @Transient
    public abstract boolean isTicketGoods();

    @Transient
    @Override
    public List<? extends me.jiangcai.goods.Goods> getAllReferencedGoods() {
        return styleList;
    }

    @Id
    @Override
    public Long getId() {
        return super.getId();
    }
}
