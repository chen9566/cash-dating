package me.jiangcai.dating.web.thymeleaf;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
@Component
public class CashStrings {

    private static final Pattern numberPattern = Pattern.compile(".*(\\d+).*");

    /**
     * @param number 真实卡号
     * @return 隐藏号的卡号
     */
    public String bankCardNumber(String number) {
        String end = number.substring(number.length() - 4);
        return "**** **** **** " + end;
    }

    public int termInteger(String term) {
        final Matcher matcher = numberPattern.matcher(term);
        matcher.matches();
        return NumberUtils.parseNumber(matcher.group(1), Integer.class);
    }
}
