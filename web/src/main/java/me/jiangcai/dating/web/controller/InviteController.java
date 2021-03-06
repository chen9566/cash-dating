package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.InviteUser;
import me.jiangcai.dating.service.StatisticService;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.List;

/**
 * 邀请相关控制器
 *
 * @author CJ
 */
@Controller
public class InviteController {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = {"/myCode"})
    @Transactional(readOnly = true)
    public String myCode(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        return "code.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/invitationNote")
    @Transactional(readOnly = true)
    public String invitationNote(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("level", userService.inviteLevel(user.getOpenId()));
        model.addAttribute("number", userService.validInvites(user.getOpenId()));
        return "note.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/myInvite", "/myInvitation"})
    @Transactional(readOnly = true)
    public String index(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        final BigDecimal balance = statisticService.balance(user.getOpenId());
        model.addAttribute("balance", balance);

        model.addAttribute("numbers", statisticService.guides(user.getOpenId()));
//        // 分开
//        String text = Common.CurrencyFormat(balance);
//        int point = text.indexOf(".");
//        model.addAttribute("number", text.substring(0, point));
//        model.addAttribute("decimal", text.substring(point));
        model.addAttribute("flows", statisticService.commissionFlows(user.getOpenId()));
        model.addAttribute("withdrawalFlows", statisticService.withdrawalFlows(user.getOpenId()));
        model.addAttribute("level", userService.inviteLevel(user.getOpenId()));

        return "myinvitation.html";

//        model.addAttribute("inviteUsers", userService.allInviteUsers(user.getOpenId()));
//        return "friends/myfriends.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myInviteCode")
    public String code(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.by(user.getId()));
        model.addAttribute("invites", userService.validInvites(user.getOpenId()));
        // 还有5个 已经完成所有条件的用户
        final List<InviteUser> inviteUserList = userService.validInviteUsers(user.getOpenId());
        int gap = 5 - inviteUserList.size();
        while (gap-- > 0) {
            inviteUserList.add(new InviteUser("", null, false));
        }
        model.addAttribute("inviteUsers", inviteUserList);
        return "friends/inviteFriends.html";
    }

}
