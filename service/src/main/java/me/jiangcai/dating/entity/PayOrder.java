package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.goods.TradedGoods;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Set;

/**
 * 支付订单，这个订单是用户为了某一事由而进行支付；这个事由肯定不是套现
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PayOrder extends CashOrder {

    /**
     * 它可能是一个商城订单
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private CashTrade saleTrade;

    @Override
    public String getProductName() {
        //找到商品，如果存在多个 则加上一个等
        final Set<TradedGoods> cashTradedGoods = saleTrade.getCashTradedGoods();
        String name = cashTradedGoods.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有商品？"))
                .getName();
        if (cashTradedGoods.size() == 1)
            return name;
        return name + "等";
    }

    @Override
    public String getSuccessURI() {
        if (saleTrade != null)
            return "/sale/paySuccess?id=" + saleTrade.getId();
        return null;
    }

    @Override
    public boolean isCashOrder() {
        return false;
    }

    @Override
    public boolean isArbitrage() {
        return false;
    }

    @Override
    public boolean isSupportAliPay() {
        return false;
    }
}
