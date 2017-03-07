package me.jiangcai.dating.model;

import me.jiangcai.dating.service.SystemService;

import java.math.BigDecimal;

/**
 * @author CJ
 */
public enum InviteLevel {


    threshold(SystemService.level1Rate, SystemService.commission1),
    senior(SystemService.level2Rate, SystemService.commission2),
    expert(SystemService.level3Rate, SystemService.commission3),
    best(SystemService.level4Rate, SystemService.commission4);

    private final BigDecimal rate;
    private final BigDecimal commission;

    InviteLevel(BigDecimal rate, BigDecimal commission) {
        this.rate = rate;
        this.commission = commission;
    }


    public BigDecimal getRate() {
        return rate;
    }

    /**
     * @return 分成系数
     */
    public BigDecimal getCommission() {
        return commission;
    }

    /**
     * @return 分佣比例
     */
    public BigDecimal getCommissionRate() {
        return SystemService.level1Rate.subtract(rate).multiply(commission);
    }
}
