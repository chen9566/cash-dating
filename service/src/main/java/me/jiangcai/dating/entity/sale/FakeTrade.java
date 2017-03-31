package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.sale.support.FakeTradedGoods;
import me.jiangcai.goods.TradedGoods;
import me.jiangcai.goods.payment.PaymentMethod;
import me.jiangcai.goods.trade.PayInfo;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class FakeTrade extends CashTrade {

    @ManyToOne
    private FakeGoods goods;
    private int count;

    @Override
    public Set<? extends TradedGoods> getGoods() {
        return Collections.singleton(new FakeTradedGoods(goods, count));
    }

    @Override
    public void addTradedGoods(TradedGoods tradedGoods) {
        goods = (FakeGoods) tradedGoods.getReferenceGoods();
        count = tradedGoods.getCount();
    }

    @Override
    public PayInfo getSuccessPay() {
        return null;
    }

    @Override
    public PayInfo getCurrentPay() {
        return null;
    }

    @Override
    public List<PaymentMethod> supportPaymentMethods() {
        return null;
    }
}
