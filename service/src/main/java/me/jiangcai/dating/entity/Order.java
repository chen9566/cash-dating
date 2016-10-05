package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 订单,此订单并不等同于支付平台的订单,指的是用户开启了一笔付款
 *
 * @author CJ
 */
@Entity
@Table(name = "_ORDER")
@Setter
@Getter
public class Order {
    @Id
    @Column(length = 32)
    private String id;
    //金额
    private BigDecimal amount;
    //备注
    @Column(length = 50)
    private String comment;
    /**
     * 开启时间
     */
    private LocalDateTime startTime;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User owner;

    /**
     * 为什么是 1对多?
     * 是考虑到一种网络互动的状况下 导致创建多笔平台订单
     */
    @OneToMany(mappedBy = "order",orphanRemoval = true,cascade = CascadeType.ALL)
    private Set<PlatformOrder> platformOrderSet;

}
