package me.jiangcai.dating.model.trj;

import lombok.Data;

/**
 * @author CJ
 */
@Data
public class MobileToken {
    /**
     * 手机号存在并且已经和云淘绑定的，则binding:1,并且token不为空
     * 手机号存在并且没有和云淘绑定的，则binding:0，则需要调用短信接口,客户确认绑定
     * 手机号不存在，则binding:-1，跳转到投融家注册页面
     */
    private int binding;
    private String mobile;
    private String token;
}
