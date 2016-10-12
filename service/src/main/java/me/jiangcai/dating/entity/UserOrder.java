package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户订单,面向用户的订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserOrder {

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
     * 提现成功了!
     */
    public abstract void withdrawalSuccess();

    /**
     * 提现失败了!
     */
    public abstract void withdrawalFailed();
}
