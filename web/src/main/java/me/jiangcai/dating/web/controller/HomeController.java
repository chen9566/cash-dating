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
 * 目前设计2个入口
 * 1 收款 既首页 不再是了 应该是/start
 * 2 我的
 *
 * @author CJ
 */
@Controller
public class HomeController {

    @Autowired
    private StatisticService statisticService;

    @RequestMapping(method = RequestMethod.GET, value = {"/start"})
    public String index() {
        return "receivables.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/my"})
    public String my(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("total", statisticService.totalExpense(user.getOpenId()));
        return "my.html";
    }


}
