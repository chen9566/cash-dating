package me.jiangcai.dating.entity.support;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * 各种几率,佣金的配置,考虑到配置会改变而业务是流动的;所以在交易发生时将当时的配置保留下来以作计算
 * 注意是rate;如果是千分之六那么应该是0.006
 *
 * @author CJ
 */
@Embeddable
@Data
public class RateConfig {

    /**
     * 用户账面手续费率,可以为0
     *
     * @see me.jiangcai.dating.Version#v101000
     * @since 1.1
     */
    @Column(scale = 7, precision = 10)
    private BigDecimal bookRate;
    /**
     * 平台手续费率,可以为0
     */
    @Column(scale = 7, precision = 10)
    private BigDecimal channelRate;
    /**
     * 合伙人佣金率,可以为0
     */
    @Column(scale = 7, precision = 10)
    private BigDecimal agentRate;
    /**
     * 发展人佣金率,可以为0
     */
    @Column(scale = 7, precision = 10)
    private BigDecimal guideRate;

    /**
     * @return 我方平台的手续费率
     */
    public BigDecimal getPlatformRate() {
        return bookRate.subtract(channelRate).subtract(agentRate).subtract(guideRate);
    }

}
