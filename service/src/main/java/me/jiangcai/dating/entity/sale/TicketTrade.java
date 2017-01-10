package me.jiangcai.dating.entity.sale;

import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.payment.PaymentMethod;
import me.jiangcai.goods.trade.PayInfo;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
public class TicketTrade extends CashTrade {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<TicketTradedGoods> tradedSet;

    @Override
    @Transient
    public Set<? extends TradedGoods> getGoods() {
        return tradedSet;
    }

    @Override
    public void addTradedGoods(TradedGoods tradedGoods) {
        if (tradedSet == null)
            tradedSet = new HashSet<>();
        tradedSet.add((TicketTradedGoods) tradedGoods);
    }

    @Override
    @Transient
    public PayInfo getSuccessPay() {
        return null;
    }

    @Override
    @Transient
    public PayInfo getCurrentPay() {
        return null;
    }

    @Override
    public List<PaymentMethod> supportPaymentMethods() {
        return null;
    }

}
