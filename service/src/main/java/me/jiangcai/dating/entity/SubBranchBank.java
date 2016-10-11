package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 支行,为了提高搜索效率,还是将它作为一个实体
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class SubBranchBank {

    public static final int CODE_LENGTH = 15;
    public static final int NAME_LENGTH = 60;

    @Id
    @Column(length = CODE_LENGTH)
    private String code;
    @Column(length = NAME_LENGTH)
    private String name;

    @ManyToOne
    private Bank bank;

    @Column(length = Address.ID_LENGTH)
    private String cityCode;


}
