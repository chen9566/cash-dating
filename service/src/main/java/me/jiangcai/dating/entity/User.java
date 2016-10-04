package me.jiangcai.dating.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.WeixinUser;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户
 *
 * @author CJ
 */
@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"mobileNumber", "openId"})})
public class User implements WeixinUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    // 业务信息
    @Column(length = 15)
    private String mobileNumber;

    // 微信信息
    @Column(length = 32)
    private String openId;
    private String accessToken;
    private LocalDateTime accessTimeToExpire;
    private String refreshToken;
    private String[] tokenScopes;

    // 这些信息是用户详情 可以随时更新
    private String nickname;
    @JsonProperty("sex")
    private Gender gender;
    private String headImageUrl;
    private String province;
    private String city;
    private String country;

    // 银行卡信息
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Card> cards;
}
