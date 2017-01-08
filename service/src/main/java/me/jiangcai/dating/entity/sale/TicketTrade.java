package me.jiangcai.dating.entity.sale;

import me.jiangcai.goods.Buyer;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.payment.PaymentMethod;
import me.jiangcai.goods.trade.PayInfo;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
public class TicketTrade extends CashTrade {

    @Override
    @Transient
    public Set<? extends TradedGoods> getGoods() {
        return null;
    }

    @Override
    public void addTradedGoods(TradedGoods tradedGoods) {

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

    @Override
    public void setBuyer(Buyer buyer) {

    }
}
