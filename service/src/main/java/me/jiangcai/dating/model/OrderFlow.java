package me.jiangcai.dating.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.model.support.OrderFlowStatus;
import me.jiangcai.goods.TradedGoods;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

/**
 * 订单流水,每一个订单流水都包括着付款,收款2个项目
 *
 * @author CJ
 */
@Setter
@Getter
@ToString
public class OrderFlow {
    private OrderFlowStatus status;
    /**
     * 主订单
     */
    private CashOrder order;
    /**
     * 最新提现订单,可选
     */
    private PlatformWithdrawalOrder withdrawalOrder;

    /**
     * @return 手续费, 只有成功才有的吧
     */
    public BigDecimal getCharge() {
        return order.getAmount().subtract(order.getWithdrawalAmount());
    }

    /**
     * @return 显示的金额
     */
    public BigDecimal getAmount() {
        if (isPayOrder())
            return order.getAmount();
        return order.getWithdrawalAmount();
    }

    /**
     * @return
     * @see CashOrder#isCashOrder()
     */
    public boolean isCashOrder() {
        return order.isCashOrder();
    }

    /**
     * @return true if 商城订单
     */
    public boolean isPayOrder() {
        return order instanceof PayOrder;
    }

    public String getPayInfo() {
        PayOrder payOrder = (PayOrder) order;
        final Set<TradedGoods> cashTradedGoods = payOrder.getSaleTrade().getCashTradedGoods();
        String name = "特卖－" + cashTradedGoods.stream()
                .findFirst().orElseThrow(() -> new IllegalStateException("没有任何商品"))
                .getName();
        if (cashTradedGoods.size() == 1)
            return name;
        return name + "等";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderFlow)) return false;
        OrderFlow orderFlow = (OrderFlow) o;
        return Objects.equals(order, orderFlow.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order);
    }
}
