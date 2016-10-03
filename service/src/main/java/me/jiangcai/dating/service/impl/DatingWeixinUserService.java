package me.jiangcai.dating.service.impl;

import me.jiangcai.wx.WeixinUserService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.SimpleWeixinUser;
import me.jiangcai.wx.model.UserAccessResponse;
import me.jiangcai.wx.model.WeixinUser;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author CJ
 */
@Service
public class DatingWeixinUserService implements WeixinUserService {
    private final HashMap<String, WeixinUser> tokens = new HashMap<>();

    @Override
    public <T> T userInfo(PublicAccount account, String openId, Class<T> clazz) {
        // 建议版本就不保存什么了 直接使用微信返回的
        if (clazz == String.class)
            //noinspection unchecked
            return (T) openId;
        if (clazz == WeixinUserDetail.class)
            return (T) Protocol.forAccount(account).userDetail(openId, this);
        throw new IllegalArgumentException(("unsupported type:" + clazz));
    }

    @Override
    public void updateUserToken(PublicAccount account, UserAccessResponse response) {
        WeixinUser user = tokens.get(response.getOpenId());
        if (user == null) {
            user = new SimpleWeixinUser();
            tokens.put(response.getOpenId(), user);
            user.setOpenId(response.getOpenId());
        }
        user.setAccessToken(response.getAccessToken());
        user.setRefreshToken(response.getRefreshToken());
        user.setTokenScopes(response.getScope());

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime.plusSeconds(response.getTime());
        user.setAccessTimeToExpire(dateTime);
    }

    @Override
    public WeixinUser getTokenInfo(PublicAccount account, String openId) {
        return tokens.get(openId);
    }
}
