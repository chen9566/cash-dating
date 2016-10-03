package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.OpenId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录页面
     * 在这里我们确定它是否要登录
     *
     * @return 登录页面
     */
    @RequestMapping(method = RequestMethod.GET, value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String login(@OpenId String id) {

        //  是否已经完成注册
        //
        if (userService.mobileRequired(id)) {
            return "register.html";
        }
        if (userService.bankAccountRequired(id)) {
            return "addcard.html";
        }
        return null;
    }



}
