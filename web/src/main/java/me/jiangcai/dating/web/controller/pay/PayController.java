package me.jiangcai.dating.web.controller.pay;

import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private OrderService orderService;

    /**
     * 开始付款--订单开始
     *
     * @param user
     * @param amount
     * @param comment
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/startOrder")
    public String start(@AuthenticationPrincipal User user, BigDecimal amount, String comment) {
        Order order = orderService.newOrder(user, amount, comment);
        return "redirect:/order/" + order.getId();
    }

    /**
     * 打开这个付款二维码展示界面,通常只有owner才会打开
     *
     * @param user
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/order/{id}")
    public String orderInfo(@AuthenticationPrincipal User user, @PathVariable("id") String id, Model model) {
        Order order = orderService.getOne(id);

        if (!order.getOwner().equals(user)) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        return "paycode.html";
    }


}
