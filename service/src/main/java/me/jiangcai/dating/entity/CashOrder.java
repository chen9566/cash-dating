package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.RateConfig;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.notify.NotifyType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 订单,此订单并不等同于支付平台的订单,指的是用户开启了一笔付款
 *
 * @author CJ
 */
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Setter
@Getter
public class CashOrder extends UserOrder {

    /**
     * 一个冗余标记位
     */
    private boolean completed;

    /**
     * 当时的几率配置
     */
    private RateConfig thatRateConfig;


    /**
     * 为什么是 1对多?
     * 是考虑到一种网络互动的状况下 导致创建多笔平台订单
     */
    @OneToMany(mappedBy = "cashOrder", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PlatformOrder> platformOrderSet;


    @Override
    public BigDecimal getWithdrawalAmount() {
        return getAmount().multiply(BigDecimal.ONE.subtract(thatRateConfig.getBookRate()));
    }

    @Override
    public void paySuccess() {
        setCompleted(true);
    }

    @Override
    public void withdrawalSuccess() {
        setWithdrawalCompleted(true);
    }

    @Override
    public void withdrawalFailed() {
        setWithdrawalCompleted(false);
    }

    @Override
    public String toString() {
        return "CashOrder{" +
                super.toString() +
//                "withdrawalCompleted=" + withdrawalCompleted +
                ", completed=" + completed +
                ", thatRateConfig=" + thatRateConfig +
                "} " + super.toString();
    }

    @Override
    public Notification withdrawalTransferNotification(PlatformWithdrawalOrder withdrawalOrder) {
        return new Notification(getOwner(), NotifyType.orderTransfer, "/orderDetail/" + getId(), this, getFriendlyId()
                , getAmount().subtract(getWithdrawalAmount()), getWithdrawalAmount()
                , withdrawalOrder.getTailNumber(), withdrawalOrder.getStartTime());
    }

    @Override
    public Notification withdrawalTransferFailedNotification(PlatformWithdrawalOrder withdrawalOrder, String reason) {
        return new Notification(getOwner(), NotifyType.orderTransferFailed, "/orderDetail/" + getId(), this, getFriendlyId()
                , getAmount().subtract(getWithdrawalAmount()), getWithdrawalAmount()
                , withdrawalOrder.getTailNumber(), withdrawalOrder.getStartTime(), reason);
    }

    /**
     * @return 本订单是否由本人收银台发起的
     */
    public boolean isCashOrder() {
        return !(this instanceof PayToUserOrder);
    }
}
