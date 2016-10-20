package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 管理
 * 入口就是/manage
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Manage_Value + "')")
@Controller
public class ManageController {

    private static final Log log = LogFactory.getLog(ManageController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, value = {"/manage", "/manage/", "/manage/user"})
    public String index(@AuthenticationPrincipal User user) {
//        if (user.getManageStatus() == null) {
//            // 非管理员无法进入
//            return "redirect:/start";
//        }
        return "manage/user.html";
    }

    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Agent_Value + "')")
    @RequestMapping(method = RequestMethod.GET, value = {"/manage/agentRequest"})
    public String agentRequest() {
        return "manage/agentRequest.html";
    }
    
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Grant_Value + "')")
    @RequestMapping(method = RequestMethod.PUT, value = "/manage/grant/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateManageStatus(@AuthenticationPrincipal User user, @PathVariable("id") Long id
            , @RequestBody String code) {
        // root 无法被授权, root 同样也无法被更改授权
        User target = userService.by(id);
        if (target.getManageStatus() != null && target.getManageStatus().isRoot())
            return;
        ManageStatus oldStatus = target.getManageStatus();
        if (code == null || code.length() == 0) {
            target.setManageStatus(null);
        } else {
            ManageStatus manageStatus = ManageStatus.valueOf(code);
            if (manageStatus.isRoot())
                return;
            target.setManageStatus(manageStatus);
        }
        userRepository.save(target);
        log.info(user.getId() + " grant " + target.getId() + " from " + oldStatus + " to " + target.getManageStatus());
    }
}
