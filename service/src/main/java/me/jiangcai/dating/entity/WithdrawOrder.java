package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.BalanceFlow;
import me.jiangcai.dating.entity.support.FlowType;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现的订单,当前订单肯定只有一个。
 * 只有被{@link WithdrawOrderStatus#cancelled 取消}而非{@link WithdrawOrderStatus#completed 完成}的订单的金额才会被无视。
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class WithdrawOrder implements BalanceFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User owner;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 开启时间
     */
    private LocalDateTime startTime;

    private WithdrawOrderStatus processStatus;
    private LocalDateTime processTime;
    private String comment;


    @Override
    @Transient
    public String getFlowName() {
        return "提现";
    }

    @Override
    @Transient
    public FlowType getFlowType() {
        return FlowType.payout;
    }
}
