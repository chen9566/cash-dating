package me.jiangcai.dating.model;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.model.support.AbstractCashOrderBalanceFlow;

import java.math.BigDecimal;

/**
 * @author CJ
 */
public class GuideCashOrderBalanceFlow extends AbstractCashOrderBalanceFlow {
    public GuideCashOrderBalanceFlow(CashOrder cashOrder) {
        super(cashOrder);
    }

    @Override
    protected BigDecimal toAmount(CashOrder cashOrder) {
        return cashOrder.getAmount().multiply(cashOrder.getThatRateConfig().getGuideRate());
    }

    @Override
    public String getFlowName() {
        return getName() + "/发展人佣金";
    }
}
