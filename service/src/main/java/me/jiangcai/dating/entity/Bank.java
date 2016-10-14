package me.jiangcai.dating.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 银行,总得管理下它们吧
 *
 * @author CJ
 */
@Entity
@Data
public class Bank {

    @Id
    @Column(length = 10)
    private String code;
    @Column(length = 20)
    private String name;
    /**
     * @see me.jiangcai.dating.Version#v102000
     * @since 1.2
     */
    private int weight = 50;
    /**
     * 背景,等同于css的background属性
     *
     * @since 1.3
     */
    @Column(length = 100)
    private String background = "linear-gradient(to right, #E75C65 , #E8507D);";

    /**
     * @return 在这个web系统的图片uri
     * @since 1.3
     */
    public String getImageUri() {
        return "/images/banks/" + name + ".png";
    }

}
