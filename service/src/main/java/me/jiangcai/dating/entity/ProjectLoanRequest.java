package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

}
