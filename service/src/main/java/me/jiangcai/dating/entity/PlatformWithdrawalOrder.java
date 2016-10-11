package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 平台的提现订单
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class PlatformWithdrawalOrder {

    @Id
    @Column(length = 32)
    private String id;

    @ManyToOne
    private UserOrder userOrder;

    /**
     * 支付系统的备注
     */
    private String comment;

    /**
     * 发起提现时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime startTime;

    /**
     * 提现完成时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime finishTime;

    // 卡片相关 这里依然采用冗余设计,不然会出现很糟糕的情况

    private Address address;

    @ManyToOne
    private Bank bank;
    /**
     * 卡号
     */
    @Column(length = 20, nullable = false)
    private String number;
    /**
     * 持卡人姓名
     */
    @Column(length = 20, nullable = false)
    private String owner;

    /**
     * 支行名称
     */
    @Column(length = SubBranchBank.NAME_LENGTH, nullable = false)
    private String subBranch;


    /**
     * @return 是否已提现完成
     */
    public abstract boolean isFinish();

    /**
     * @return 是否已提现成功
     */
    public abstract boolean isSuccess();

}
