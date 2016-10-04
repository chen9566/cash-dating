package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单,此订单并不等同于支付平台的订单,指的是用户开启了一笔付款
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Order {
    @Id
    private String id;
    //金额
    private BigDecimal amount;
    //备注
    private String comment;
    /**
     * 开启时间
     */
    private LocalDateTime startTime;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User owner;


}
