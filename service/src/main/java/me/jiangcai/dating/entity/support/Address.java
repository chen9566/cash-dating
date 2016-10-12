package me.jiangcai.dating.entity.support;

import lombok.Data;
import me.jiangcai.chanpay.model.City;
import me.jiangcai.chanpay.model.Province;

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

    public static final int ID_LENGTH = 10;

    @Column(length = ID_LENGTH)
    private Province province;
    @Column(length = ID_LENGTH)
    private City city;

}
