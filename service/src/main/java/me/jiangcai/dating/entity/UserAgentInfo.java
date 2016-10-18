package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

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

}
