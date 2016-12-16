package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CJ
 */
@Controller
public class ManagePasswordController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/manage/password")
    public String index() {
        return "manage/password.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/manage/password")
    @Transactional
    public String change(@AuthenticationPrincipal User user, @RequestParam(required = false) String mobile
            , String password) {
        if (!StringUtils.isEmpty(mobile) && (user.getManageStatus().isRoot()
                || user.getManageStatus().roles().contains(Login.Role_Grant_Value))) {
            user = userService.byMobile(mobile);
        } else {
            user = userService.by(user.getId());
        }

        userService.updatePassword(user, password);
        return "redirect:/manage/password";
    }


}
