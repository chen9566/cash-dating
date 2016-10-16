package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.model.support.FlowType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现的订单,当前订单肯定只有一个。
 * 只有被{@link WithdrawOrderStatus#cancelled 取消}而非{@link WithdrawOrderStatus#completed 完成}的订单的金额才会被无视。
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class WithdrawOrder extends UserOrder implements BalanceFlow {

    private WithdrawOrderStatus processStatus = WithdrawOrderStatus.requested;
    @Column(columnDefinition = "datetime")
    private LocalDateTime processTime;

    @Override
    @Transient
    public String getFlowName() {
        return "提现";
    }

    @Override
    @Transient
    public FlowType getFlowType() {
        return FlowType.payout;
    }

    @Override
    public BigDecimal getWithdrawalAmount() {
        return getAmount();
    }

    @Override
    public void paySuccess() {
        // 不可能
    }

    @Override
    public void withdrawalSuccess() {
        setProcessStatus(WithdrawOrderStatus.completed);
    }

    @Override
    public void withdrawalFailed() {
        setProcessStatus(WithdrawOrderStatus.cancelled);
    }
}
