package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chanpay.model.TradeStatus;

import javax.persistence.Entity;

import static me.jiangcai.chanpay.model.TradeStatus.TRADE_SUCCESS;

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
        return status == TRADE_SUCCESS;
//        return status == PAY_FINISHED ||  || status == TRADE_FINISHED;
    }
}
