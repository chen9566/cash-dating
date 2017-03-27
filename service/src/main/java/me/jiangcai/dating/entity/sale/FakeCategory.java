package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 伪装类目
 *
 * @author CJ
 */
@Setter
@Getter
@Entity
public class FakeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, unique = true)
    private String name;
    /**
     * 表现出来的样式
     */
    @Column(length = 10)
    private String className;
    /**
     * 商品权重
     */
    private int weight;

}
