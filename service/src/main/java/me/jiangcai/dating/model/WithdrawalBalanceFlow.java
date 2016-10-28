package me.jiangcai.dating.model;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.model.support.AbstractCashOrderBalanceFlow;

import java.math.BigDecimal;

/**
 * 自己订单的成功提现流水
 *
 * @author CJ
 */
public class WithdrawalBalanceFlow extends AbstractCashOrderBalanceFlow {

    public WithdrawalBalanceFlow(CashOrder cashOrder) {
        super(cashOrder);
    }

    @Override
    protected BigDecimal toAmount(CashOrder cashOrder) {
        return cashOrder.getWithdrawalAmount();
    }

    @Override
    public String getFlowName() {
        return "提现";
    }
}
