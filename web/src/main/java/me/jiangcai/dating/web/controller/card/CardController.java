package me.jiangcai.dating.web.controller.card;

import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.OpenId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 银行卡相关控制器
 * 1,银行只有一张
 * 2,绑定绝对是主动动作,所以每次绑定都会带一个跳转URL用于绑定完成以后
 *
 * @author CJ
 */
@Controller
public class CardController {

    private static final String RedirectSessionKey = "me.jc.dating.card.redirect";
    private static final String NextActionSessionKey = "me.jc.dating.card.nextAction";

    @Autowired
    private UserService userService;
    @Autowired
    private CardService cardService;
    @Autowired
    private BankService bankService;

    @RequestMapping(method = RequestMethod.GET, value = "/myBank")
    public String index(@AuthenticationPrincipal User user, Model model) {
        final User user1 = userService.byOpenId(user.getOpenId());
        model.addAttribute("user", user1);
        final Card recommend = cardService.recommend(user1);
        if (recommend != null)
            model.addAttribute("cards", Collections.singletonList(recommend));
        else
            model.addAttribute("cards", new ArrayList<>());
        return "bankcard.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/card")
    public String start(@RequestHeader(value = "Referer", required = false) String redirectUrl, String nextAction
            , HttpSession session, Model model) {
        if (!StringUtils.isEmpty(redirectUrl) || !StringUtils.isEmpty(nextAction)) {
            if (StringUtils.isEmpty(nextAction)) {
                session.removeAttribute(NextActionSessionKey);
            } else
                session.setAttribute(NextActionSessionKey, nextAction);

            if (StringUtils.isEmpty(redirectUrl)) {
                session.removeAttribute(RedirectSessionKey);
            } else
                session.setAttribute(RedirectSessionKey, redirectUrl);
        }

        model.addAttribute("banks", bankService.list());
        return "addcard.html";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/registerCard")
    @Transactional
    public String registerCard(@OpenId String id, HttpSession session, String name, String number
                               // 这3个参数 其实用不上
            , String province, String city, String bank
            , String subBranch) {
//
//        Address address = new Address();
//        address.setProvince(PayResourceService.provinceById(province));
//        address.setCity(PayResourceService.cityById(city));

//        userService.deleteCards(id);
        // 禁用原卡
        cardService.disableRecommendCard(id);

        Card card = cardService.addCard(id, name, number, null, null, subBranch);

        String action = (String) session.getAttribute(NextActionSessionKey);
        if (!StringUtils.isEmpty(action))
            return "redirect:" + action;
        String url = (String) session.getAttribute(RedirectSessionKey);
        if (StringUtils.isEmpty(url))
            return "redirect:/start";
        return "redirect:" + url;
    }

}
