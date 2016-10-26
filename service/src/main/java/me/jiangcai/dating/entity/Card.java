package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * 是指银行卡
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Card {

    public static final int NUMBER_LENGTH = 30;
    public static final int OWNER_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 选择的银行
     */
    @ManyToOne(optional = false)
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
     * 开户行地址
     */
    private Address address;

    /**
     * 支行编号,我们是允许用户手工输入支行名称,所以此处可以为null
     */
    @ManyToOne
    private SubBranchBank subBranchBank;
    /**
     * 支行名称
     */
    @Column(length = SubBranchBank.NAME_LENGTH, nullable = false)
    private String subBranch;

    /**
     * 是否已禁用（被替换）
     * @since 1.5
     * @see me.jiangcai.dating.Version#v105000
     */
    private boolean disabled;

    /**
     * @return 尾号
     */
    public String getTailNumber() {
        int length = number.length();
        return number.substring(length - 4);
    }

    @Override
    public String toString() {
        return "Card{" +
                "bank=" + bank +
                ", number='" + number + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(bank, card.bank) &&
                Objects.equals(number, card.number) &&
                Objects.equals(owner, card.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bank, number, owner);
    }
}
