package me.jiangcai.dating.service;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.thymeleaf.util.NumberPointType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
@Component
public class CashStrings {

    private static final Pattern numberPattern = Pattern.compile("(\\d+).*");

    /**
     * @param number 真实卡号
     * @return 隐藏号的卡号
     */
    public String bankCardNumber(String number) {
        String end = number.substring(number.length() - 4);
        return "**** **** **** " + end;
    }

    /**
     * @param number 价格
     * @return 商城价格
     */
    public String mallPrice(Number number) {
        return org.thymeleaf.util.NumberUtils.format(number, 1, NumberPointType.COMMA,
                2, NumberPointType.POINT, Locale.CHINA);
    }

    /**
     * @param id
     * @return 隐藏身份证号码
     */
    public String idNumber(String id) {
        StringBuilder stringBuilder = new StringBuilder(id.substring(0, 1));
        // 最后剩下2个 那么剩下的是?
        int count = id.length() - 2 - 1;
        while (count-- > 0)
            stringBuilder.append("*");
        stringBuilder.append(id.substring(id.length() - 3));
        return stringBuilder.toString();
    }

    public int termInteger(String term) {
        final Matcher matcher = numberPattern.matcher(term);
        matcher.matches();
        return NumberUtils.parseNumber(matcher.group(1), Integer.class);
    }
}
