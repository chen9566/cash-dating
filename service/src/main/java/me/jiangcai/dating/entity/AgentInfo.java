package me.jiangcai.dating.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 代理商,并不作为一个替代User的实体,而是作为额外信息
 *
 * @author CJ
 */
@Entity
public class AgentInfo {

    // 可能还需要些实际的信息吧

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 我们在结算代理商佣金的时间 应该是不会把成为代理商之前的订单也算给它吧
     * 成为代理商的时间
     * TODO
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime joinTime;

}
