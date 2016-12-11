package me.jiangcai.dating.entity.channel;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chrone.model.PayStatus;
import me.jiangcai.dating.entity.PlatformOrder;

import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ChroneOrder extends PlatformOrder {

    private PayStatus payStatus;

    @Override
    public boolean isFinish() {
        return payStatus == PayStatus.closed || payStatus == PayStatus.success || payStatus == PayStatus.failed;
    }
}
