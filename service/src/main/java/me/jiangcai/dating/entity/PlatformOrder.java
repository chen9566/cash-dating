package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.channel.PayChannel;

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
    /**
     * 平台方的id,通常我们不会给予管理
     *
     * @since 1.8.1
     */
    @Column(length = 32)
    private String platformId;

    @ManyToOne
    private CashOrder cashOrder;

    /**
     * 支付平台提供的支付链接
     */
    private String url;

    /**
     * 完成时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime finishTime;

    /**
     * @return 是否已支付完成
     */
    public abstract boolean isFinish();

    /**
     * @return 负责运作套现的渠道类
     */
    public abstract Class<? extends ArbitrageChannel> arbitrageChannelClass();

    public abstract Class<? extends PayChannel> payChannelClass();
}
