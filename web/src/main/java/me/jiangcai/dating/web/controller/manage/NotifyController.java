package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 管理通知的
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Edit_Value + "')")
@Controller
@RequestMapping("/manage/notify")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("templateMessages", notifyService.allTemplate());
        return "manage/notify.html";
    }


}
