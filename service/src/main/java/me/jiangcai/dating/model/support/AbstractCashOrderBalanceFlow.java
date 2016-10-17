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

    private final String name;
    private final BigDecimal originalAmount;
    private final String comment;
    private final LocalDateTime startTime;
    private final BigDecimal amount;
    public AbstractCashOrderBalanceFlow(CashOrder cashOrder) {
        comment = cashOrder.getComment();
        startTime = cashOrder.getStartTime();
        amount = toAmount(cashOrder);
        name = cashOrder.getOwner().getRealName();
        originalAmount = cashOrder.getAmount();
    }

    /**
     * @param cashOrder 订单
     * @return 这个单子的收益
     */
    protected abstract BigDecimal toAmount(CashOrder cashOrder);

    @Override
    public FlowType getFlowType() {
        return FlowType.revenue;
    }
}
