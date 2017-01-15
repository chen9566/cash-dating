package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.payment.PaymentMethod;
import me.jiangcai.goods.trade.PayInfo;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class TicketTrade extends CashTrade {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<TicketTradedGoods> tradedSet;

    /**
     * @return 已排序的code
     */
    public List<TicketCode> getOrderedCodes() {
        List<TicketCode> list = tradedSet.stream().map(TicketTradedGoods::getCodeSet)
                .collect(ArrayList::new, List::addAll, List::addAll);
        Collections.sort(list);
        return list;
    }

    public Map<TicketGoods, List<TicketCode>> getMappedCodes() {
        List<TicketCode> list = getOrderedCodes();
        // http://www.importnew.com/17313.html
        return list.stream().collect(Collectors.groupingBy(code -> code.getBatch().getGoods()));
    }

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
