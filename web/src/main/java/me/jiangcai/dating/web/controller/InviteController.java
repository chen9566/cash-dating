package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

/**
 * 邀请相关控制器
 *
 * @author CJ
 */
@Controller
public class InviteController {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/myInvite")
    public String index(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        final BigDecimal balance = statisticService.balance(user.getOpenId());
        model.addAttribute("balance", balance);
        // 分开
        String text = Common.CurrencyFormat(balance);
        int point = text.indexOf(".");
        model.addAttribute("number", text.substring(0, point));
        model.addAttribute("decimal", text.substring(point));
        model.addAttribute("flows", statisticService.balanceFlows(user.getOpenId()));
        return "mymoney.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myInviteCode")
    public String code(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        return "code.html";
    }

}
