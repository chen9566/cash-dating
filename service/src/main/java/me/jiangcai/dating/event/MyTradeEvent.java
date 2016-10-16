package me.jiangcai.dating.event;

import lombok.Data;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.dating.Locker;

/**
 * @author CJ
 */
@Data
public class MyTradeEvent implements Locker {

    private final TradeEvent event;

    @Override
    public Object lockObject() {
        return event.getSerialNumber().intern();
    }
}
