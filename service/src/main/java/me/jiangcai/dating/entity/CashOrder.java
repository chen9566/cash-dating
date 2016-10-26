package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.RateConfig;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
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
     * 另一个,呵呵
     */
    private boolean withdrawalCompleted;

    /**
     * 当时的几率配置
     */
    private RateConfig thatRateConfig;

    @ManyToOne
    private Card card;

    /**
     * 为什么是 1对多?
     * 是考虑到一种网络互动的状况下 导致创建多笔平台订单
     */
    @OneToMany(mappedBy = "cashOrder", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PlatformOrder> platformOrderSet;

    @OneToMany(mappedBy = "userOrder", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<PlatformWithdrawalOrder> platformWithdrawalOrderSet;

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
                "withdrawalCompleted=" + withdrawalCompleted +
                ", completed=" + completed +
                ", card=" + card +
                ", thatRateConfig=" + thatRateConfig +
                "} " + super.toString();
    }
}
