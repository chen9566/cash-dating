package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.BookRateLevel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 用户信息的扩展,代理人相关
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class UserAgentInfo {

    @Id
    private Long id;

    private BookRateLevel bookLevel;

    /**
     * 更新时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime updateTime;

}
