package me.jiangcai.dating.web.controller.my;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

/**
 * 提现相关
 *
 * @author CJ
 */
@Controller
public class WithdrawController {

    @Autowired
    private CardService cardService;
    @Autowired
    private UserService userService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, value = "/withdraw")
    @Transactional(readOnly = true)
    public String index(@AuthenticationPrincipal User user, Model model) {
        if (cardService.bankAccountRequired(user.getOpenId())) {
            return "redirect:/card?nextAction=/withdraw";
        }
        // 如果没有
        user = userService.by(user.getId());

        model.addAttribute("card", cardService.recommend(user));
        model.addAttribute("balance", statisticService.balance(user.getOpenId()));
        return "now.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/withdraw")
//    @Transactional
    public String withdraw(@AuthenticationPrincipal User user, BigDecimal amount) throws IOException, SignatureException {
        user = userService.by(user.getId());
        orderService.newWithdrawOrder(user, amount, null);
        return "redirect:/withdrawList";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/withdrawList")
    @Transactional(readOnly = true)
    public String list(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("withdrawalFlows", statisticService.withdrawalFlows(user.getOpenId()));
        return "Cashlist.html";
    }

}
