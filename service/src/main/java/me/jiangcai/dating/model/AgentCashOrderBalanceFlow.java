package me.jiangcai.dating.model;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.model.support.AbstractCashOrderBalanceFlow;

import java.math.BigDecimal;

/**
 * @author CJ
 */
public class AgentCashOrderBalanceFlow extends AbstractCashOrderBalanceFlow {

    public AgentCashOrderBalanceFlow(CashOrder cashOrder) {
        super(cashOrder);
    }

    @Override
    protected BigDecimal toAmount(CashOrder cashOrder) {
        return cashOrder.getAmount().multiply(cashOrder.getThatRateConfig().getAgentRate());
    }

    @Override
    public String getFlowName() {
        return getName() + "/合伙人佣金";
    }
}
