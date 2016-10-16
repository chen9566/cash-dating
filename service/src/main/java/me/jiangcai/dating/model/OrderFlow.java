package me.jiangcai.dating.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.model.support.OrderFlowStatus;

import java.util.Objects;

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
