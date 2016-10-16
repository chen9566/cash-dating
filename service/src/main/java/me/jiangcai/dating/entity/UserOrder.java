package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.Locker;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户订单,面向用户的订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserOrder implements Locker {

    @Id
    @Column(length = 32)
    private String id;
    //金额
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;
    //备注
    @Column(length = 50)
    private String comment;
    /**
     * 开启时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime startTime;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User owner;

    /**
     * @return 以提现的立场来说, 它的金额应该是多少
     */
    public abstract BigDecimal getWithdrawalAmount();

    /**
     * 支付成功!
     */
    public abstract void paySuccess();

    /**
     * 提现成功了!
     */
    public abstract void withdrawalSuccess();

    /**
     * 提现失败了!
     */
    public abstract void withdrawalFailed();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOrder)) return false;
        UserOrder userOrder = (UserOrder) o;
        return Objects.equals(id, userOrder.id) &&
                Objects.equals(amount, userOrder.amount) &&
                Objects.equals(owner, userOrder.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, owner);
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", amount=" + amount +
                ", comment='" + comment + '\'' +
                ", startTime=" + startTime +
                ", owner=" + owner +
                ',';
    }

    @Override
    public Object lockObject() {
        return getId().intern();
    }
}
