package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.channel.PayChannel;
import me.jiangcai.dating.service.ChanpayService;

import javax.persistence.Entity;

import static me.jiangcai.chanpay.model.TradeStatus.*;

/**
 * 畅捷订单,是指在支付平台建立的订单
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ChanpayOrder extends PlatformOrder {

    /**
     * 畅捷支付状态
     */
    private TradeStatus status;

    @Override
    public boolean isFinish() {
        return status == PAY_FINISHED || status == TRADE_SUCCESS || status == TRADE_FINISHED;
    }

    @Override
    public Class<? extends ArbitrageChannel> arbitrageChannelClass() {
        return ChanpayService.class;
    }

    @Override
    public Class<? extends PayChannel> payChannelClass() {
        return ChanpayService.class;
    }
}
