package me.jiangcai.dating.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.model.CashWeixinUserDetail;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.WeixinUser;
import me.jiangcai.wx.model.WeixinUserDetail;

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
import java.util.Objects;

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
    // 最后一次获取真实微信详情的时间
    private LocalDateTime lastRefreshDetailTime;

    // 银行卡信息
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Card> cards;


    public void updateWeixinUserDetail(WeixinUserDetail detail) {
        setLastRefreshDetailTime(LocalDateTime.now());
        setCountry(detail.getCountry());
        setProvince(detail.getProvince());
        setCity(detail.getCity());
        setNickname(detail.getNickname());
        setGender(detail.getGender());
        setHeadImageUrl(detail.getHeadImageUrl());
    }

    /**
     * 从数据库拉去,当然我们可以不拉
     *
     * @return 微信用户详情
     */
    public CashWeixinUserDetail resolveWeixinUserDetail() {
        if (nickname == null)
            return null;
        if (gender == null)
            return null;
        if (headImageUrl == null)
            return null;
        if (lastRefreshDetailTime == null)
            return null;
        // 超过1礼拜了
        LocalDateTime nextWeek = lastRefreshDetailTime.plusWeeks(1);
        if (LocalDateTime.now().isAfter(nextWeek))
            return null;
        CashWeixinUserDetail detail = new CashWeixinUserDetail();
        detail.setCountry(country);
        detail.setProvince(province);
        detail.setCity(city);
        detail.setNickname(nickname);
        detail.setOpenId(openId);
        detail.setGender(gender);
        detail.setHeadImageUrl(headImageUrl);
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(mobileNumber, user.mobileNumber) &&
                Objects.equals(openId, user.openId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobileNumber, openId);
    }

}
