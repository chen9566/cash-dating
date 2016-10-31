package me.jiangcai.dating.model.trj;

import lombok.Data;

/**
 * @author CJ
 */
@Data
public class Loan {
    private String productId;
    private String amount;
    private String[] term;
    private String introduce;
    private String condition;
    private String region;
    private String productName;
}
