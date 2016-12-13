package me.jiangcai.dating.channel;

import me.jiangcai.chrone.event.PayStatusChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public interface ChroneService extends ArbitrageChannel {

    @EventListener(PayStatusChangeEvent.class)
    @Transactional
    void change(PayStatusChangeEvent event);
}
