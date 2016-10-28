package me.jiangcai.dating.model;

import lombok.Data;
import me.jiangcai.dating.model.support.OrderFlowStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * 某一段时间的合集
 *
 * @author CJ
 */
@Data
public class OrderFlows {

    private final List<OrderFlow> flows;
    private final String flag;

    public BigDecimal getTotal() {
        return flows.stream()
                .filter(orderFlow -> orderFlow.getStatus() == OrderFlowStatus.success)
                .map(orderFlow -> orderFlow.getOrder().getWithdrawalAmount())
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalStateException::new);
    }


}
