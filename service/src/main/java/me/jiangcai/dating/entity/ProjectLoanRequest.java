package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.service.WealthService;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
     * 手机号码是否已验证
     */
    private boolean mobileVerified;

    /**
     * @param builder CriteriaBuilder
     * @param root    root
     * @return 判断已经完成签章
     */
    public static Predicate HasSignedPredicate(CriteriaBuilder builder, Root<?> root) {
//        Subquery subquery;
//        builder.sub
        return builder.greaterThanOrEqualTo(builder.size(root.get("contracts")), WealthService.ContractElements.size());
//        ArrayList<Predicate> predicateArrayList = new ArrayList<>(WealthService.ContractElements.size());
////        builder.keys()
//        Expression<Set<String>> cs = root.get("contracts");
//        for (String c : WealthService.ContractElements) {
////            MapJoin<ProjectLoanRequest, String, String> map = root.joinMap("contracts", JoinType.LEFT);
////            map = builder.treat(map, String.class);
////            predicateArrayList.add(builder.and(builder.equal(map.key(), c), builder.isNotNull(map.value())));
//            predicateArrayList.add(builder.isMember(c, cs));
//        }
//        return builder.and(predicateArrayList.toArray(new Predicate[predicateArrayList.size()]));
    }

    /**
     * @return 到期还款
     */
    public BigDecimal toReturn() {
        return getAmount().multiply(yearRate).multiply(new BigDecimal(termDays))
                .divide(new BigDecimal("365"), BigDecimal.ROUND_HALF_UP).add(getAmount());
    }

    public Notification toRejectNotification() {
        String comment = StringUtils.isEmpty(getComment()) ? "拒绝" : getComment();
        return new Notification(getLoanData().getOwner(), NotifyType.projectLoanRejected, null, getId()
                , getLoanData().getName()
                , "项目贷款"
                , applyAmount
                , comment
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
