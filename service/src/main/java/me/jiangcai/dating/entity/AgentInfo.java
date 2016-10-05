package me.jiangcai.dating.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

}
