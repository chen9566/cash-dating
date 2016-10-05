package me.jiangcai.dating.web.thymeleaf;

import org.springframework.stereotype.Component;

/**
 * @author CJ
 */
@Component
public class CashStrings {

    /**
     * @param number 真实卡号
     * @return 隐藏号的卡号
     */
    public String bankCardNumber(String number) {
        String end = number.substring(number.length() - 4);
        return "**** **** **** " + end;
    }
}
