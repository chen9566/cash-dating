package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.AgentRequestStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * 申请成为代理商的请求
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class AgentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 谁干的
     */
    @ManyToOne
    private User from;

    @Column(length = 20)
    private String name;
    @Column(length = 20)
    private String mobileNumber;

    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;

    private AgentRequestStatus processStatus;
    @Column(columnDefinition = "datetime")
    private LocalDateTime processTime;
    private String comment;
}
