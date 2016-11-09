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
import java.util.Objects;

import static me.jiangcai.dating.entity.Card.NUMBER_LENGTH;
import static me.jiangcai.dating.entity.Card.OWNER_LENGTH;

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
    @Column(length = NUMBER_LENGTH, nullable = false)
    private String number;
    /**
     * 持卡人姓名
     */
    @Column(length = OWNER_LENGTH, nullable = false)
    private String owner;

    /**
     * 支行名称
     */
    @Column(length = SubBranchBank.NAME_LENGTH, nullable = false)
    private String subBranch;

    /**
     * @return 尾号
     */
    public String getTailNumber() {
        int length = number.length();
        return number.substring(length - 4);
    }

    /**
     * @return 是否已提现完成
     */
    public abstract boolean isFinish();

    /**
     * @return 是否已提现成功
     */
    public abstract boolean isSuccess();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatformWithdrawalOrder)) return false;
        PlatformWithdrawalOrder that = (PlatformWithdrawalOrder) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userOrder, that.userOrder) &&
                Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userOrder, startTime);
    }
}
