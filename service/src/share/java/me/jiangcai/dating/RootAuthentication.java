/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package me.jiangcai.dating;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;

/**
 * @author CJ
 */
public class RootAuthentication implements Authentication {

    public static final RootAuthentication root = new RootAuthentication();

    private RootAuthentication() {
    }

    /**
     * 以root身份运行一段代码。只允许在初始化或者登录校验身份或者执行schedule时使用。
     *
     * @param work 工作代码
     */
    public static void runAsRoot(Runnable work) {
        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(RootAuthentication.root);
        try {
            work.run();
        } finally {
            if (SecurityContextHolder.getContext().getAuthentication() == RootAuthentication.root) {
                SecurityContextHolder.getContext().setAuthentication(oldAuthentication);
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ROOT"));
    }

    @Override
    public Object getCredentials() {
        return this;
    }

    @Override
    public Object getDetails() {
        return this;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "root";
    }
}
