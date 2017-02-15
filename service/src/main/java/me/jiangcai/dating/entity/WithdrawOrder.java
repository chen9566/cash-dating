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
 * 只有未完成{@link #reallyWithdrawalCompletedPredicate()}而且已经结束{@link #reallyWithdrawalFinishPredicate()}的订单才会被视作无效提现订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class WithdrawOrder extends UserOrder implements BalanceFlow {

    /**
     * 不再依赖这个状态
     * 在以后版本中将移除
     */
    @Deprecated
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
        if (isWithdrawalCompleted())
            return "完成";
        if (!reallyWithdrawalFinishPredicate().test(this))
            return "进行中";
        return "已取消";
//        switch (processStatus) {
//            case cancelled:
//                return "已取消";
//            case completed:
//                return "完成";
//            case requested:
//                return "进行中";
//        }
//        return null;
    }

    @Override
    public String getProductName() {
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
        return new Notification(getOwner(), NotifyType.withdrawalTransfer, "/withdrawList", this, getFriendlyId(), getWithdrawalAmount()
                , withdrawalOrder.getTailNumber(), withdrawalOrder.getStartTime());
    }

    @Override
    public Notification withdrawalTransferFailedNotification(PlatformWithdrawalOrder withdrawalOrder, String reason) {
        return new Notification(getOwner(), NotifyType.withdrawalTransferFailed, "/withdrawList", this, getFriendlyId(), getWithdrawalAmount()
                , withdrawalOrder.getTailNumber(), withdrawalOrder.getStartTime(), reason);
    }
}
