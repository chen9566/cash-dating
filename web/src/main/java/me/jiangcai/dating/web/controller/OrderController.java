package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.CashOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
public class OrderController {

    @Autowired
    private CashOrderRepository cashOrderRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/orderList")
    public String index(@AuthenticationPrincipal User user) {
        // select * from cashorder where owner=me
        return "order.html";
    }

}
