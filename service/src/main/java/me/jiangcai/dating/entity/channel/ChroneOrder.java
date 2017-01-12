package me.jiangcai.dating.entity.channel;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chrone.model.PayStatus;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.channel.ChroneService;
import me.jiangcai.dating.channel.PayChannel;
import me.jiangcai.dating.entity.PlatformOrder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ChroneOrder extends PlatformOrder {

    private PayStatus status;
    @Column(scale = 2, precision = 20)
    private BigDecimal fee;

    @Override
    public boolean isFinish() {
        return status == PayStatus.closed || status == PayStatus.success || status == PayStatus.failed;
    }

    @Override
    public Class<? extends ArbitrageChannel> arbitrageChannelClass() {
        return ChroneService.class;
    }

    @Override
    public Class<? extends PayChannel> payChannelClass() {
        throw new NoSuchMethodError("Chrone Not Support Pay");
    }
}
