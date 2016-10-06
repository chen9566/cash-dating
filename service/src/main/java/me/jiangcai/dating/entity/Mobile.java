package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 电话号码,这里有一个很好玩的概念
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Mobile {
    /**
     * 就是电话号码啦
     */
    @Id
    @Column(length = 15)
    private String number;

    /**
     * 最近的验证码
     */
    @Column(length = 10)
    private String code;
    /**
     * 验证码过期时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime codeExpireTime;
}
