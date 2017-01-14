package me.jiangcai.dating.web.controller.my;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
public class MyController {


    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemService systemService;

    @RequestMapping(method = RequestMethod.GET, value = {"/my"})
    public String my(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        model.addAttribute("total", statisticService.totalExpense(user.getOpenId()));
//        model.addAttribute("orders", statisticService.countCashOrder(user.getOpenId()));
//        model.addAttribute("applyCardUrl", systemService.getApplyCardUrl());
        return "my.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/myData"})
    public String data(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        return "mydata.html";
    }
}
