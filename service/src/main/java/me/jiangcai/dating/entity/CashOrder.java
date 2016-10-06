package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.RateConfig;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 订单,此订单并不等同于支付平台的订单,指的是用户开启了一笔付款
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class CashOrder {
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

}
