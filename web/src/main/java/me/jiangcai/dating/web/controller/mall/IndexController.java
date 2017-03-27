package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 允许非授权访问的假商城
 *
 * @author CJ
 */
@RequestMapping("/mall")
@Controller
public class IndexController {

    @Autowired
    private UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String home() {
        return "/mall/index.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public String register() {
        return "/mall/register.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    @Transactional
    public String doRegister(HttpServletRequest request, String mobile, String verificationCode, String password
            , RedirectAttributes redirectAttributes) {
        // 这里详细的逻辑是，如果用户不存在，则建立该用户并且设置它的登录密码
        // 如果用户已存在， 并且密码已设置 则提示已注册；如果密码未设置 则设置该密码
        // 为此配套的话，需要公众号那边 也需要对手机号码已存在用户 做兼容性（即检查openId如果为空 则继续！）
        try {
            verificationCodeService.verify(mobile, verificationCode, VerificationType.register);
        } catch (IllegalVerificationCodeException ignored) {
            redirectAttributes.addFlashAttribute("_message", "验证码错误");
            return "redirect:/mall/register";
        }
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

}
