package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.Locker;
import me.jiangcai.dating.entity.support.LoanRequestStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借款申请
 *
 * @author CJ
 */
@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class LoanRequest implements Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private UserLoanData loanData;
    /**
     * 项目的id
     */
    @Column(length = 30)
    private String projectId;
    /**
     * 项目名称
     */
    @Column(length = 30)
    private String projectName;
    @Column(scale = 2, precision = 20)
    private BigDecimal amount;
    /**
     * 还款周期
     */
    private int months;
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;
    private boolean completed;
    /**
     * 供应商已受理,并且提供了他们的id
     */
    @Column(length = 30)
    private String supplierRequestId;
    private LoanRequestStatus processStatus = LoanRequestStatus.requested;
    @Column(columnDefinition = "datetime")
    private LocalDateTime processTime;
    private String comment;
    @ManyToOne
    private User processor;

    @Override
    public Object lockObject() {
        return ("LoanRequest-" + id).intern();
    }
}
