package me.jiangcai.dating.web.controller.auth;

import com.google.zxing.WriterException;
import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.web.controller.GlobalController;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author CJ
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;
    ///////////////////////////////非微信登录
    @Autowired
    private QRCodeService qrCodeService;

    @RequestMapping(method = RequestMethod.GET, value = "/justLogout")
    public String logout() {
        // 已登出页面
        return "logout.html";
    }

    /**
     * 登录页面
     * 在这里我们确定它是否要登录
     *
     * @return 登录页面
     */
    @RequestMapping(method = RequestMethod.GET, value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String login(WeixinUserDetail detail, Model model, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        userService.updateWeixinDetail(detail, request);
        //  是否已经完成注册
        //
        if (userService.mobileRequired(detail.getOpenId())) {
            Long invite = CashFilter.inviteBy(request);
            if (invite != null) {
                model.addAttribute("invite", userService.by(invite));
            } else
                model.addAttribute("invite", null);
            return "register.html";
        }
//        if (userService.bankAccountRequired(detail.getOpenId())) {
//            model.addAttribute("banks", bankService.list());
//            return "addcard.html";
//        }

        userService.loginAs(request, response, userService.byOpenId(detail.getOpenId()));
        // 完成登录
//        return "redirect:/";
        return null;

    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerMobile")
    public String registerMobile(HttpServletRequest request, @OpenId String id, String mobile, String verificationCode
            , String inviteCode) {
        userService.registerMobile(request, id, mobile, verificationCode, inviteCode);

        return "redirect:/start";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loginToken/{id}")
    public BufferedImage tokenQR(HttpServletRequest request, @PathVariable("id") String id)
            throws IOException, WriterException {
        StringBuilder urlBuilder = GlobalController.contextUrlBuilder(request);
        urlBuilder.append("/approvalLogin/").append(id);

        return qrCodeService.generateQRCode(urlBuilder.toString());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/loginToken/{id}")
    @ResponseBody
    public int checkLoginRequest(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") long id) {
        try {
            userService.checkRequestLogin(id, request, response);
        } catch (ServletException e) {
            return 1;
        } catch (IOException e) {
            return 1;
        } catch (IllegalStateException e) {
            return 1;
        } catch (IllegalArgumentException e) {
            return -1;
        }
        return 0;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/approvalLogin/{id}")
    public String approvalLogin(@AuthenticationPrincipal User user, @PathVariable("id") long id) {
        // TODO 还需要确认么? 麻烦死
        userService.approvalLogin(id, user);
        return "redirect:/start";
    }

}
