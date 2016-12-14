package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.notify.NotifyType;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 项目贷款
 * 和普通贷款相比较,它的数据都会被调整,所有增加了申请时数据
 * 另外它还需要签名,所以存在合同编号
 *
 * @author CJ
 * @since 1.8.0
 */
@ToString
@Setter
@Getter
@Entity
public class ProjectLoanRequest extends LoanRequest {

    @Column(scale = 2, precision = 20)
    private BigDecimal applyAmount;
    /**
     * 周期,单位是天
     */
    private int termDays;
    private int applyTermDays;
    private int creditLimitYears;
    private int applyCreditLimitYears;
    @ElementCollection
    private Map<String, String> contracts;
    //再加入 年化利率
    @Column(scale = 2, precision = 20)
    private BigDecimal yearRate;

    /**
     * @return 到期还款
     */
    public BigDecimal toReturn() {
        return getAmount().multiply(yearRate).multiply(new BigDecimal(termDays))
                .divide(new BigDecimal("365"), BigDecimal.ROUND_HALF_UP).add(getAmount());
    }

    public Notification toRejectNotification() {
        return new Notification(getLoanData().getOwner(), NotifyType.projectLoanRejected, null, getId()
                , getLoanData().getName()
                , "项目贷款"
                , applyAmount
                , "拒绝"
        );
    }

    public Notification toAcceptNotification() {
        return new Notification(getLoanData().getOwner(), NotifyType.projectLoanAccepted, "/projectLoan?id=" + getId(), getId()
                , getLoanData().getName()
                , "项目贷款"
                , applyAmount
                , "接受"
        );
    }
}
