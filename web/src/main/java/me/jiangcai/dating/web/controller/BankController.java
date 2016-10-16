package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 银行卡相关动作
 *
 * @author CJ
 */
@Controller
public class BankController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/myBank")
    public String index(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.byOpenId(user.getOpenId()));
        return "bankcard.html";
    }
}
