package me.jiangcai.dating.model.support;

import java.math.BigDecimal;

/**
 * @author CJ
 */
public enum FlowType {
    /**
     * 收入
     */
    revenue,
    /**
     * 开支
     */
    payout;

    /**
     * @return 这个数应该*什么
     */
    public BigDecimal toMultiply() {
        if (this == revenue)
            return BigDecimal.ONE;
        return BigDecimal.ONE.negate();
    }

    public String toFlag() {
        if (this == revenue)
            return "+";
        return "-";
    }
}
