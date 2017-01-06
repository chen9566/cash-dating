package me.jiangcai.dating.model.trj;

import lombok.Data;

/**
 * 1.0.6增加amount,opinion
 *
 * @author CJ
 */
@Data
public class LoanStatusResult {
    private String status;
    private String opinion;
    private String amount;

    public LoanStatus toLoanStatus() {
        switch (status) {
            case "2":
                return LoanStatus.failed;
            case "3":
                return LoanStatus.success;
            default:
                return LoanStatus.auditing;
        }
    }

}
