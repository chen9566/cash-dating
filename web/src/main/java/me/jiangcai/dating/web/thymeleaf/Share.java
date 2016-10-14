package me.jiangcai.dating.web.thymeleaf;

import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.web.controller.GlobalController;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 分享的帮助
 *
 * @author CJ
 */
@Component
public class Share {

    public String title(CashOrder order) {
        return order.getComment() != null ? order.getComment() : "买单";
    }

    public String desc(CashOrder order) {
        return order.getOwner().getUsername() + "请您买单";
    }

    public String link(CashOrder order, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url + "?" + CashFilter.guideUserFromId(order.getOwner().getId());
    }

    public String imageUrl(CashOrder order) {
        return "http://www.kuanyes.com/images/ky_logo2.png";
    }


    public String title(User user) {
        return desc(user);
    }

    public String desc(User user) {
        return user.getUsername() + "邀请你加入款爷!";
    }

    public String link(User user, HttpServletRequest request) {
        return GlobalController.generateInviteURL(user.getId(), request).toString();
//        return "http://app.kuanyes.com/cash/start" + "?" + CashFilter.guideUserFromId(user.getId());
//        String url = request.getRequestURL().toString();
//        return url + "?" + CashFilter.guideUserFromId(user.getId());
    }

    public String imageUrl(User user) {
        return "http://www.kuanyes.com/images/ky_logo2.png";
    }

}
