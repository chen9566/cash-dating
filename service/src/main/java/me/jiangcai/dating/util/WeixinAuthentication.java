package me.jiangcai.dating.util;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 微信认证,它理论上应该只保存一个openId
 *
 * @author CJ
 */
public class WeixinAuthentication implements Authentication {

    private final String openId;
    private final UserService userService;
    private User user;

    public WeixinAuthentication(User user, UserService userService) {
        this.user = user;
        this.openId = user.getOpenId();
        this.userService = userService;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        forUser();
        return user.getAuthorities();
    }

    private void forUser() {
        if (user == null)
            user = userService.byOpenId(openId);
    }

    @Override
    public Object getCredentials() {
        return openId;
    }

    @Override
    public Object getDetails() {
        forUser();
        return user;
    }

    @Override
    public Object getPrincipal() {
        forUser();
        return user;
    }

    @Override
    public boolean isAuthenticated() {
//        user = null;
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        forUser();
        return user.getUsername();
    }
}
