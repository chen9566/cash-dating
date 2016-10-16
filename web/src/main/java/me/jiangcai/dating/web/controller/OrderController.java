package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CJ
 */
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/orderList")
    public String index(@AuthenticationPrincipal User user, Model model) {
        List<Card> cards = userService.byOpenId(user.getOpenId()).getCards();
        if (cards == null) {
            cards = new ArrayList<>();
        }
        model.addAttribute("orders", orderService.orderFlows(user.getOpenId()));
        model.addAttribute("cards", cards);
        return "order.html";
    }

}
