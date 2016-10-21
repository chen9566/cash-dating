package me.jiangcai.dating.entity.support;

import java.math.BigDecimal;

/**
 * 账面手续费费等级
 * 0.45%
 * 0.5%
 * 0.6%
 *
 * @author CJ
 */
public enum BookRateLevel {

    threshold("0.6%"),
    senior("0.5%"),
    expert("0.45%"),
    punishment("0.72%");

    private String message;

    BookRateLevel(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return message;
    }

    public BigDecimal toRate() {
        String x = message.substring(0, message.length() - 1);
        return new BigDecimal(x).movePointLeft(2);
        //noinspection BigDecimalMethodWithoutRoundingCalled
//        return new BigDecimal(x).divide(BigDecimal.valueOf(100L));
    }
}
