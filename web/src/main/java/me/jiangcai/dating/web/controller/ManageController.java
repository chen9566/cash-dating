package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 管理
 * 入口就是/pc
 *
 * @author CJ
 */
@Controller
public class ManageController {

    @RequestMapping(method = RequestMethod.GET, value = {"/pc", "/pc/"})
    public String index(@AuthenticationPrincipal User user) {
        if (user.getManageStatus() == null) {
            // 非管理员无法进入
            return "redirect:/start";
        }

        return "other/index.html";
    }
}
