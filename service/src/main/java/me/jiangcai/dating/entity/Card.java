package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 选择的银行
     */
    @ManyToOne
    private Bank bank;
    /**
     * 卡号
     */
    private String number;
    /**
     * 持卡人姓名
     */
    private String owner;

    private String type;

}
