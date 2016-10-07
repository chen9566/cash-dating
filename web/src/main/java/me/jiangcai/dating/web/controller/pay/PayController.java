package me.jiangcai.dating.web.controller.pay;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

/**
 * 支付相关
 *
 * @author CJ
 */
@Controller
public class PayController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ChanpayService chanpayService;

    /**
     * 开始付款--订单开始
     *
     * @param user
     * @param amount
     * @param comment
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/startOrder")
    public String start(@AuthenticationPrincipal User user, BigDecimal amount, String comment) {
        CashOrder order = orderService.newOrder(user, amount, comment);
        return "redirect:/order/" + order.getId();
    }

    /**
     * 打开这个付款二维码展示界面,开放显示
     * 这个页面应该会存在多个效果  比如微信平台的效果和非微信平台的效果
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/order/{id}")
    public String orderInfo(@PathVariable("id") String id, Model model) throws IOException, SignatureException {
        if (orderService.isComplete(id)) {
            return "completed.html";
        }
        CashOrder order = orderService.getOne(id);

//        if (!order.getOwner().equals(user)) {
//            return "redirect:/";
//        }
        // 直接建立订单
        PlatformOrder platformOrder = orderService.preparePay(id, PayChannel.weixin);

        if (platformOrder instanceof ChanpayOrder) {
            model.addAttribute("qrUrl", chanpayService.QRCodeImageFromOrder((ChanpayOrder) platformOrder));
        } else
            throw new IllegalStateException("no chanpayOrder??" + platformOrder);

        model.addAttribute("order", order);
        return "paycode.html";
    }


}
