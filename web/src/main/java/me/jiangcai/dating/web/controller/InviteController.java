package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 邀请相关控制器
 * @author CJ
 */
@Controller
public class InviteController {

    @Autowired
    private StatisticService statisticService;

    @RequestMapping(method = RequestMethod.GET, value = "/myInvite")
    public String index(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("user",user);
        model.addAttribute("balance", statisticService.balance(user.getOpenId()));
        model.addAttribute("flows", statisticService.balanceFlows(user.getOpenId()));
        return "mymoney.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myInviteCode")
    public String code(@AuthenticationPrincipal User user, Model model){
        return "code.html";
    }

}
