package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 允许非授权访问的假商城
 *
 * @author CJ
 */
@RequestMapping("/mall")
@Controller
public class IndexController {

    public static final String MallMode = "MallMode";

    @Autowired
    private UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String home(HttpSession session) {

        session.setAttribute(MallMode, true);
        return "/mall/index.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public String register(@RequestParam(required = false) Integer type, Model model) {
        if (type != null) {
            // 1
            model.addAttribute("_message", "你输入了错误的验证码");
        }
        return "/mall/register.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    @Transactional
    public String doRegister(HttpServletRequest request, String mobile, String verificationCode, String password
            , RedirectAttributes redirectAttributes) {
        // 这里详细的逻辑是，如果用户不存在，则建立该用户并且设置它的登录密码
        // 如果用户已存在， 并且密码已设置 则提示已注册；如果密码未设置 则设置该密码
        // 为此配套的话，需要公众号那边 也需要对手机号码已存在用户 做兼容性（即检查openId如果为空 则继续！）
        verificationCodeService.verify(mobile, verificationCode, VerificationType.register);

        User user = userService.byMobile(mobile);

        if (user == null) {
            user = userService.newUser(null, request);
        } else {
            if (user.getPassword() != null) {
                // 消息怎么写给客户端呢？
                redirectAttributes.addFlashAttribute("_message", "用户已存在,请登录");
                return "redirect:/mall/register";
            }
        }

        userService.updatePassword(user, password);
        redirectAttributes.addFlashAttribute("_message", "欢迎加入款爷！");
        return "redirect:/mall";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(String type, Model model) {
        if (type != null) {
            model.addAttribute("_error", "用户名或者密码错误");
        }
        return "/mall/login.html";
    }

}
