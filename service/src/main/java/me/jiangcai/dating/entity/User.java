package me.jiangcai.dating.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.WeixinUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author CJ
 */
@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "mobileNumber")})
public class User implements WeixinUser {
    // 业务信息
    @Column(length = 15)
    private String mobileNumber;
    @Id
    @Column(length = 30)
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
}
