package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class PlatformOrder {
    @Id
    @Column(length = 32)
    private String id;

    @ManyToOne
    private Order order;

    /**
     * 支付平台提供的支付链接
     */
    private String url;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     *
     * @return 是否已支付完成
     */
    public abstract boolean isFinish();
}
