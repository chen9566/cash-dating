package me.jiangcai.dating.util;

import org.thymeleaf.util.NumberPointType;
import org.thymeleaf.util.NumberUtils;

import java.util.Locale;

/**
 * @author CJ
 */
public class Common {

    /**
     * 一般状态下货币格式化的结果
     *
     * @param number 金额
     * @return 字符串
     */
    public static String CurrencyFormat(Number number) {
        return NumberUtils.format(number, 1, NumberPointType.COMMA, 2, NumberPointType.POINT, Locale.CHINA);
    }
}
