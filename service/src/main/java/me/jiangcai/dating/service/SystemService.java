package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.support.RateConfig;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统级别
 *
 * @author CJ
 */
public interface SystemService {

    /**
     * @return 当前的配置
     */
    @Transactional(readOnly = true)
    RateConfig currentRateConfig();

    /**
     * 更新配置,并不会影响已下的订单
     *
     * @param rateConfig
     */
    @Transactional
    void updateRateConfig(RateConfig rateConfig);
}
