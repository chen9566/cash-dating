package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

import static me.jiangcai.dating.entity.Card.OWNER_LENGTH;

/**
 * 用于用于借款的数据
 *
 * @author CJ
 */
@Entity
@Getter
@Setter
public class UserLoanData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User owner;
    /**
     * 真实姓名
     */
    @Column(length = OWNER_LENGTH, nullable = false)
    private String name;
    /**
     * 身份证号码
     */
    @Column(length = 20)
    private String number;
    /**
     *
     */
    private Address address;
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;
    @Column(columnDefinition = "datetime")
    private LocalDateTime lastUseTime;


}
