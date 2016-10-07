package me.jiangcai.dating.service;

import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.TokenType;
import me.jiangcai.wx.model.PublicAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 我的公众号
 *
 * @author CJ
 */
@Component
public class CashPublicAccount extends PublicAccount implements PublicAccountSupplier {

    private static final String AccessToken = "dating.weixin.accessToken";
    private static final String JavascriptTicket = "dating.weixin.javascriptTicket";
    private static final String TimeToExpire = "dating.weixin.timeToExpire";
    private static final String JavascriptTimeToExpire = "dating.weixin.javascriptTimeToExpire";
    @Autowired
    private SystemService systemService;
    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        setAppID(environment.getRequiredProperty("cash.weixin.appId"));
        setAppSecret(environment.getRequiredProperty("cash.weixin.appSecret"));
        setInterfaceURL(environment.getRequiredProperty("cash.weixin.url"));
        setInterfaceToken(environment.getRequiredProperty("cash.weixin.token"));
        //
        setAccessToken(systemService.getSystemString(AccessToken, null, null));
        setJavascriptTicket(systemService.getSystemString(JavascriptTicket, null, null));
        setTimeToExpire(systemService.getSystemString(TimeToExpire, LocalDateTime.class, null));
        setJavascriptTimeToExpire(systemService.getSystemString(JavascriptTimeToExpire, LocalDateTime.class, null));
    }

    @Override
    public PublicAccountSupplier getSupplier() {
        return this;
    }

    @Override
    public List<PublicAccount> getAccounts() {
        return Collections.singletonList(this);
    }

    @Override
    public PublicAccount findByIdentifier(String identifier) {
        return this;
    }

    @Override
    public void updateToken(PublicAccount account, TokenType type, String token, LocalDateTime timeToExpire) throws Throwable {
        if (type == TokenType.access) {
            systemService.updateSystemString(AccessToken, token);
            systemService.updateSystemString(TimeToExpire, timeToExpire);
        } else if (type == TokenType.javascript) {
            systemService.updateSystemString(JavascriptTicket, token);
            systemService.updateSystemString(JavascriptTimeToExpire, timeToExpire);
        }
    }

    @Override
    public void getTokens(PublicAccount account) {
// 不用了 我在构造中执行了
    }

    @Override
    public PublicAccount findByHost(String host) {
        return this;
    }
}
