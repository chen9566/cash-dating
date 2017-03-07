package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.supplier.Pay123Card;
import me.jiangcai.dating.exception.ArbitrageBindFailedException;
import me.jiangcai.dating.exception.ArbitrageBindRequireException;
import me.jiangcai.dating.exception.ArbitrageBindingException;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
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
    private UserService userService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private CardService cardService;

    @RequestMapping(method = RequestMethod.GET, value = "/start123")
    public String start123(@AuthenticationPrincipal User user, Model model) throws UnsupportedEncodingException {
        Pay123Card card = userService.updatePay123Card(user.getOpenId());
        if (card != null) {
            model.addAttribute("qrUrl", "/toQR?text=" + URLEncoder.encode(card.getQrUrl(), "UTF-8"));
            return "paycode123.html";
        }
        return "redirect:/start";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/start"})
    public String index(@AuthenticationPrincipal User user, Model model, @RequestParam(required = false) Long cardId)
            throws IOException, SignatureException, ArbitrageBindFailedException, ArbitrageBindingException, ArbitrageBindRequireException {
        user = userService.byOpenId(user.getOpenId());

        if (systemService.isEnablePay123()) {
            Pay123Card card = userService.updatePay123Card(user.getOpenId());
            if (card != null) {
                model.addAttribute("qrUrl", "/toQR?text=" + URLEncoder.encode(card.getQrUrl(), "UTF-8"));
                return "paycode123.html";
            }
        }

        // 虽然我们无法预测用户必然使用微信,但是也就差不多吧
        final ArbitrageChannel channel = systemService.arbitrageChannel(PayMethod.weixin);
        if (channel.useOneOrderForPayAndArbitrage()) {
            // 绑定的状态 尚未进行 则提示是否确认绑定
            if (cardId != null)
                channel.bindUser(user);

            switch (channel.bindingUserStatus(user)) {
                case notYet:
                    throw new ArbitrageBindRequireException(channel.debitCardManageable());
                case auditing:
                    throw new ArbitrageBindingException();
            }
            // 绑定中 则提示
        }

        if (user.getCards() != null && !user.getCards().isEmpty()) {
            model.addAttribute("card", cardService.recommend(user));
            model.addAttribute("cards", user.getCards());
        } else {
            model.addAttribute("card", null);
            model.addAttribute("cards", new ArrayList<>());
        }

        // 还有一个数字
        model.addAttribute("lowestAmount", channel.lowestAmount());
        model.addAttribute("rate", systemService.systemBookRate(user));

        return "receivables.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/explain")
    public String explain(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        return "explain.html";
    }


}
