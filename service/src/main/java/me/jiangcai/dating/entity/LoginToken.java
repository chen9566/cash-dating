package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 非核心业务,用于非微信平台登录依据的
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class LoginToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 开启的时间,最多5分钟 不然本次扫码登录就取消
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;

    /**
     * 有人许可了。
     */
    @ManyToOne
    private User approval;


}
