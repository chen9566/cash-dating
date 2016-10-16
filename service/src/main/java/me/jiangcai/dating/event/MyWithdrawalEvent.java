package me.jiangcai.dating.event;

import lombok.Data;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.dating.Locker;

/**
 * @author CJ
 */
@Data
public class MyWithdrawalEvent implements Locker {
    private final WithdrawalEvent event;

    @Override
    public Object lockObject() {
        return event.getSerialNumber().intern();
    }
}
