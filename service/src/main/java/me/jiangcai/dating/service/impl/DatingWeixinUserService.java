package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.WeixinUserService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.UserAccessResponse;
import me.jiangcai.wx.model.WeixinUser;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Service
public class DatingWeixinUserService implements WeixinUserService {
//    private final HashMap<String, WeixinUser> tokens = new HashMap<>();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public <T> T userInfo(PublicAccount account, String openId, Class<T> clazz, Object data) {
        // 建议版本就不保存什么了 直接使用微信返回的
        if (clazz == String.class)
            //noinspection unchecked
            return (T) openId;
        if (clazz == WeixinUserDetail.class) {
            User user = userService.byOpenId(openId);
            if (user == null)
                //noinspection unchecked
                return (T) Protocol.forAccount(account).userDetail(openId, this, data);
            WeixinUserDetail detail = user.resolveWeixinUserDetail();
            if (detail != null)
                //noinspection unchecked
                return (T) detail;
            //noinspection unchecked
            return (T) Protocol.forAccount(account).userDetail(openId, this, data);
        }

        throw new IllegalArgumentException(("unsupported type:" + clazz));
    }

    @Override
    public void updateUserToken(PublicAccount account, UserAccessResponse response, Object data) {
        User user = userService.byOpenId(response.getOpenId());
        if (user == null) {
            HttpServletRequest request;
            if (data == null)
                request = null;
            else if (data instanceof NativeWebRequest) {
                request = ((NativeWebRequest) data).getNativeRequest(HttpServletRequest.class);
            } else if (data instanceof HttpServletRequest)
                request = (HttpServletRequest) data;
            else
                request = null;
            user = userService.newUser(response.getOpenId(), request);
        }
        user.setAccessToken(response.getAccessToken());
        user.setRefreshToken(response.getRefreshToken());
        user.setTokenScopes(response.getScope());

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime.plusSeconds(response.getTime());
        user.setAccessTimeToExpire(dateTime);

        userRepository.save(user);
    }

    @Override
    public WeixinUser getTokenInfo(PublicAccount account, String openId) {
        return userRepository.findByOpenId(openId);
    }
}
