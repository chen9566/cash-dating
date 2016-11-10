package me.jiangcai.dating.model.trj;

import lombok.Data;
import org.springframework.util.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
@Data
public class Loan {

    private static final Pattern amount10KPattern = Pattern.compile("(\\d+).*");
    private String productId;
    private String amount;
    private String[] term;
    private String introduce;
    private String condition;
    private String region;
    private String productName;

    public String getAmount10K() {
        if (!amount.contains("万"))
            throw new IllegalArgumentException(amount);
        final Matcher matcher = amount10KPattern.matcher(amount);
        matcher.matches();
        return matcher.group(1);
    }

    public int getAmountInteger() {
        return NumberUtils.parseNumber(getAmount10K(), Integer.class) * 10000;
    }
}
