package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.entity.support.RateConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统级别
 *
 * @author CJ
 */
public interface SystemService {


    /**
     * 自动调度
     */
    @PostConstruct
    @Transactional
    void init() throws UnsupportedEncodingException;

    /**
     * @param profitSplit 利润分配者
     * @return 当前的配置
     */
    @Transactional(readOnly = true)
    RateConfig currentRateConfig(ProfitSplit profitSplit);

    /**
     * @param profitSplit 相关
     * @return 账面手续费
     */
    BigDecimal systemBookRate(ProfitSplit profitSplit);

    /**
     * 获取配置值
     *
     * @param key          key
     * @param exceptedType 期待的类型
     * @param defaultValue 默认数据
     * @param <T>          范型
     * @return 默认数据或者当前值
     */
    @Transactional(readOnly = true)
    <T> T getSystemString(String key, Class<T> exceptedType, T defaultValue);

    /**
     * 更新配置,并不会影响已下的订单
     *
     * @param rateConfig
     */
    @Transactional
    void updateRateConfig(RateConfig rateConfig);

    @Transactional
    void updateSystemString(String key, String value);

    @Transactional
    void updateSystemString(String key, LocalDateTime value);
}
