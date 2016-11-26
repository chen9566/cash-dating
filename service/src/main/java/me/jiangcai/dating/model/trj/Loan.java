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

    /**
     * @return 产品图片的URI
     */
    public String getImageUri() {
        if (productName.equals("快车宝"))
            return "/images/Loan-icon.png";
        if (productName.equals("随心贷"))
            return "/images/Loan-icon1.png";
        if (productName.equals("购车宝"))
            return "/images/Loan-icon2.png";
        //不知道什么图片的情况下
        return "/images/Loan-con4.png";
    }

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
