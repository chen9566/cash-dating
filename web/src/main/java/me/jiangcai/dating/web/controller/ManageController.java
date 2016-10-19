package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 管理
 * 入口就是/manage
 * http://localhost:63342/cash-dating/web/src/main/webapp/mock/users.json?search=ok&sort=city&order=asc&offset=0&limit=10
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Manage_Value + "')")
@Controller
public class ManageController {

    @RequestMapping(method = RequestMethod.GET, value = {"/manage", "/manage/"})
    public String index(@AuthenticationPrincipal User user) {
        if (user.getManageStatus() == null) {
            // 非管理员无法进入
            return "redirect:/start";
        }

        return "manage/user.html";
    }
}
