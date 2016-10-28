package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;

    // 订单详情
    @RequestMapping(method = RequestMethod.GET, value = "/orderDetail/{id}")
    public String detail(@AuthenticationPrincipal User user, Model model, @PathVariable("id") String id) {
        final CashOrder order = orderService.getOne(id);
        model.addAttribute("order", order);
        model.addAttribute("successPlatformOrder",
                order.getPlatformWithdrawalOrderSet().stream()
                        .filter(PlatformWithdrawalOrder::isSuccess).findFirst().orElse(null));
        model.addAttribute("workingPlatformOrder",
                order.getPlatformWithdrawalOrderSet().stream()
                        .filter(platformWithdrawalOrder -> !platformWithdrawalOrder.isFinish()).findFirst().orElse(null));
        model.addAttribute("platformOrder", order.getPlatformOrderSet().stream()
                .filter(PlatformOrder::isFinish).findFirst().orElseThrow(IllegalStateException::new));
        // 是否允许重试
        return "orderdetails.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/orderList")
    public String index(@AuthenticationPrincipal User user, Model model) {
        if (cardService.bankAccountRequired(user.getOpenId())) {
            // 这个时候去绑卡,并且设定回调
            return "redirect:/card?nextAction=/orderList";
        }
//        List<Card> cards = userService.byOpenId(user.getOpenId()).getCards();
//        if (cards == null) {
//            cards = new ArrayList<>();
//        }
        model.addAttribute("orders", orderService.orderFlows(user.getOpenId()));
//        model.addAttribute("cards", cards);
        return "myorder.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/touchOrder")
    public String touch(@RequestParam String id, Long cardId) throws IOException, SignatureException {
        orderService.withdrawalWithCard(id, cardId);
        return "redirect:/orderList";
    }

}
