package me.jiangcai.dating.web.controller.pay;

import me.jiangcai.dating.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

/**
 * 支付相关
 *
 * @author CJ
 */
@Controller
public class PayController {


    @RequestMapping(method = RequestMethod.POST, value = "/startOrder")
    public String start(@AuthenticationPrincipal User user, BigDecimal amount, String comment) {
        return "redirect:/order/xxxxxx";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/order/{id}")
    public String orderInfo(@AuthenticationPrincipal User user, @PathVariable("id") String id) {
        return "paycode.html";
    }


}
