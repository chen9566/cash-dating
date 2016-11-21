package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.model.support.FlowType;
import me.jiangcai.dating.notify.NotifyType;

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

    private WithdrawOrderStatus processStatus = WithdrawOrderStatus.cancelled;
    @Column(columnDefinition = "datetime")
    private LocalDateTime processTime;

    @Override
    @Transient
    public String getFlowName() {
        return getOwner().getRealName() + "提现";
    }

    @Override
    @Transient
    public FlowType getFlowType() {
        return FlowType.payout;
    }

    @Override
    public BigDecimal getOriginalAmount() {
        return getAmount();
    }

    @Override
    public String getStatus() {
        switch (processStatus) {
            case cancelled:
                return "已取消";
            case completed:
                return "完成";
            case requested:
                return "进行中";
        }
        return null;
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
        setWithdrawalCompleted(true);
        setProcessStatus(WithdrawOrderStatus.completed);
    }

    @Override
    public void withdrawalFailed() {
        setWithdrawalCompleted(false);
        setProcessStatus(WithdrawOrderStatus.cancelled);
    }

    @Override
    public Notification withdrawalTransferNotification(PlatformWithdrawalOrder withdrawalOrder) {
        return new Notification(getOwner(), NotifyType.withdrawalTransfer, null, this, getFriendlyId(), getWithdrawalAmount()
                , withdrawalOrder.getBank().getCode(), withdrawalOrder.getStartTime());
    }

    @Override
    public Notification withdrawalTransferFailedNotification(PlatformWithdrawalOrder withdrawalOrder, String reason) {
        return new Notification(getOwner(), NotifyType.withdrawalTransferFailed, null, this, getFriendlyId(), getWithdrawalAmount()
                , withdrawalOrder.getBank().getCode(), withdrawalOrder.getStartTime(), reason);
    }
}
