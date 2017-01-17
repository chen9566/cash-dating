package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.goods.Buyer;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.core.entity.Trade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CashTrade extends Trade {
    // 设计一个编号风格吧


    /**
     * 购买者
     */
    @ManyToOne(optional = false)
    private User user;
    @OneToMany(mappedBy = "saleTrade")
    private Set<PayOrder> payOrderSet;
    /**
     * 是否已支付的冗余字段
     */
    private boolean paidSuccess;

    public static Predicate belongUser(User user, CriteriaBuilder criteriaBuilder, Path<? extends CashTrade> tradeRoot) {
        return criteriaBuilder.equal(tradeRoot.get("user"), user);
    }

    @SuppressWarnings("unchecked")
    public Set<TradedGoods> getCashTradedGoods() {
        return (Set<TradedGoods>) getGoods();
    }

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
