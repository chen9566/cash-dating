package me.jiangcai.dating.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.Locker;
import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.model.CashWeixinUserDetail;
import me.jiangcai.dating.model.InviteLevel;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.goods.Buyer;
import me.jiangcai.wx.model.Gender;
import me.jiangcai.wx.model.WeixinUser;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户,当然一个代理商也是一个用户
 * 每一个用户都会有一个引导者,也会有一个区域代理
 *
 * @author CJ
 */
@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"mobileNumber", "openId", "inviteCode"})})
public class User implements WeixinUser, ProfitSplit, UserDetails, Locker, Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 管理信息
    private ManageStatus manageStatus;
    /**
     * @see me.jiangcai.dating.Version#v102001
     * @since 1.2
     */
    private boolean enabled = true;

    // 价值信息
    /**
     * 结算后的余额
     * 像实时订单信息 在一定时间以后 会被清盘
     * 比如月中,统计上个月所有账单会是一个不错的办法
     * 简单的说就是{@link BalanceFlow}并不包含在这个值
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal settlementBalance = BigDecimal.ZERO;
    /**
     * 不同于{@link #settlementBalance 余额},这个表示所有赚来的钱
     *
     * @since 1.5
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal settlementRevenue = BigDecimal.ZERO;

    /**
     * 不同于{@link #settlementBalance 余额},这个表示所有成功的提现
     *
     * @since 1.5
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal settlementWithdrawal = BigDecimal.ZERO;

    /**
     * 跟上面那个一样,不过这里是开支
     */
    @Column(scale = 2, precision = 20)
    private BigDecimal settlementExpense = BigDecimal.ZERO;

    // 业务信息
    @Column(length = 15)
    private String mobileNumber;
    /**
     * 邀请码
     */
    @Column(length = 7)
    private String inviteCode;
    /**
     * 只有代理商才会存在
     */
    @OneToOne(cascade = CascadeType.ALL)
    private AgentInfo agentInfo;
    /**
     * 引导者
     */
    @ManyToOne
    private User guideUser;
    /**
     * 所属代理商,这个人必然存在{@link #agentInfo}
     */
    @ManyToOne
    private User agentUser;
    /**
     * 所在区域所设置的扩展信息
     */
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserAgentInfo myAgentInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserPaymentExtend userPaymentExtend;

    // 微信信息
    @Column(length = 32)
    private String openId;
    @Column(length = 120)
    private String accessToken;
    @Column(columnDefinition = "datetime")
    private LocalDateTime accessTimeToExpire;
    @Column(length = 120)
    private String refreshToken;
    private String tokenScopeStr;

    // 这些信息是用户详情 可以随时更新
    @Column(length = 150)
    private String nickname;
    @JsonProperty("sex")
    private Gender gender;
    private String headImageUrl;
    @Column(length = 20)
    private String province;
    @Column(length = 20)
    private String city;
    @Column(length = 20)
    private String country;
    // 最后一次获取真实微信详情的时间
    @Column(columnDefinition = "datetime")
    private LocalDateTime lastRefreshDetailTime;

    // 杂项
    /**
     * 注册时间
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime joinTime;
    /**
     * 最后一次登录
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime loginTime;
    private String password;

    // 银行卡信息
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Card> cards;

    // 借款信息
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<UserLoanData> userLoanDataList;

    /**
     * 有效用户的谓语
     *
     * @param criteriaBuilder cb
     * @param userPath
     * @return 谓语
     */
    public static Predicate validUserPredicate(CriteriaBuilder criteriaBuilder, Path<User> userPath) {
        return criteriaBuilder.isNotNull(userPath.get("mobileNumber"));
    }

    public UserAgentInfo updateMyAgentInfo() {
        if (myAgentInfo != null)
            return myAgentInfo;
        UserAgentInfo userAgentInfo = new UserAgentInfo();
        userAgentInfo.setId(getId());
        return userAgentInfo;
    }

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

    @Override
    public String[] getTokenScopes() {
        String str = getTokenScopeStr();
        if (str == null)
            return null;
        return str.split(",");
    }

    @Override
    public void setTokenScopes(String[] strings) {
        if (strings == null) {
            setTokenScopeStr(null);
        } else
            setTokenScopeStr(String.join(",", (CharSequence[]) strings));
    }

    @Override
    public boolean useLowestRate() {
        // 很少有人有
        return manageStatus != null && (manageStatus == ManageStatus.root);
    }

    @Override
    public BigDecimal guideRate(UserService userService) {
        if (guideUser == null)
            return null;
        return guideUser.inviteLevel(userService).getCommissionRate();
    }

    @Override
    public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
        return inviteLevel(userService).getRate();
//        if (systemService.hasInviteValidUser(openId, 5))
//            return systemService.systemPreferentialRate();
//        return null;
//        if (myAgentInfo == null)
//            return null;
//        if (myAgentInfo.getBookLevel() == null)
//            return null;
//
//        return myAgentInfo.getBookLevel().toRate();
    }

    @Override
    public InviteLevel inviteLevel(UserService userService) {
        return userService.inviteLevel(getOpenId());
    }

    @Override
    public double agentProfileRate(SystemService systemService) {
        if (agentUser == null)
            return 0;
//        return agentUser.agentInfo != null ? 0.8 : 0.2;
        return 0;
    }

    @Override
    public double guideProfileRate(SystemService systemService) {
        return 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return manageStatus == null
                ? Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                : manageStatus.roles().stream()
                .map(role -> {
                    role = role.toUpperCase(Locale.CHINA);
                    if (role.startsWith("ROLE_"))
                        return role;
                    return "ROLE_" + role;
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * @return 真实名字
     */
    public String getRealName() {
        String name;
        if (cards != null && !cards.isEmpty())
            name = cards.get(0).getOwner();
        else
            name = getUsername();
        return "*" + name.substring(1);
    }

    @Override
    public String getUsername() {
        return nickname == null ? (mobileNumber == null ? openId : mobileNumber) : nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Object lockObject() {
        return ("UserLock-" + getId()).intern();
    }
}
