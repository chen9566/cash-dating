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
     * @return 尾号
     */
    public String getTailNumber() {
        int length = number.length();
        return number.substring(length - 4);
    }

}
