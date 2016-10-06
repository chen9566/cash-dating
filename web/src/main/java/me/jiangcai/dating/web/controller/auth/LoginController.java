package me.jiangcai.dating.web.controller.auth;

import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            return "register.html";
        }
        if (userService.bankAccountRequired(detail.getOpenId())) {
            model.addAttribute("banks", bankService.list());
            return "addcard.html";
        }

        userService.loginAs(request, response, userService.byOpenId(detail.getOpenId()));
        // 完成登录
//        return "redirect:/";
        return null;

    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerMobile")
    public String registerMobile(HttpServletRequest request, @OpenId String id, String mobile, String verificationCode
            , String inviteCode) {
        userService.registerMobile(request, id, mobile, verificationCode, inviteCode);

        return "redirect:/login";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registerCard")
    public String registerCard(@OpenId String id, String name, String number, String province, String city, String bank
            , String subBranch) {
        Address address = new Address();
        address.setProvince(province);
        address.setCity(city);

        Card card = userService.addCard(id, name, number, bankService.byCode(bank), address, subBranch);
        return "redirect:/login";
    }


}
