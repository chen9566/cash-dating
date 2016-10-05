package me.jiangcai.dating.entity.support;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 地址
 *
 * @author CJ
 */
@Embeddable
@Data
public class Address {

    @Column(length = 20)
    private String province;
    @Column(length = 20)
    private String city;

}
