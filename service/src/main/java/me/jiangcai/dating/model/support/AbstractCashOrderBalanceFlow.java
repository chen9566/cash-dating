package me.jiangcai.dating.model.support;

import lombok.Data;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.model.BalanceFlow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Data
public abstract class AbstractCashOrderBalanceFlow implements BalanceFlow {

    public AbstractCashOrderBalanceFlow(CashOrder cashOrder) {
        comment = cashOrder.getComment();
        startTime = cashOrder.getStartTime();
        amount = toAmount(cashOrder);
    }

    /**
     * @param cashOrder 订单
     * @return 这个单子的收益
     */
    protected abstract BigDecimal toAmount(CashOrder cashOrder);

    private String comment;
    private LocalDateTime startTime;
    private BigDecimal amount;

    @Override
    public FlowType getFlowType() {
        return FlowType.revenue;
    }
}
