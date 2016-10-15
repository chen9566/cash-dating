package me.jiangcai.dating.web.controller;

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

import java.util.ArrayList;

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
    @Autowired
    private UserService userService;
    @Autowired
    private SystemService systemService;

    @RequestMapping(method = RequestMethod.GET, value = {"/start"})
    public String index(@AuthenticationPrincipal User user, Model model) {
        user = userService.byOpenId(user.getOpenId());
        if (user.getCards() != null && !user.getCards().isEmpty()) {
            model.addAttribute("card", user.getCards().get(0));
            model.addAttribute("cards", user.getCards());
        } else {
            model.addAttribute("card", null);
            model.addAttribute("cards", new ArrayList<>());
        }

        // 还有一个数字
        model.addAttribute("rate", systemService.systemBookRate(user));

        return "receivables.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/my"})
    public String my(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("total", statisticService.totalExpense(user.getOpenId()));
        return "my.html";
    }


}
