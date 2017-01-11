package me.jiangcai.dating.web.controller.pay;

import com.google.zxing.WriterException;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.web.controller.GlobalController;
import me.jiangcai.wx.OpenId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.time.LocalDateTime;

/**
 * 支付相关
 *
 * @author CJ
 */
@Controller
public class PayController {

    @Autowired
    private OrderService orderService;
    //    @Autowired
//    private ChanpayService chanpayService;
    @Autowired
    private UserService userService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET, value = "/payToMe")
    public String payToMe(@AuthenticationPrincipal User user, Model model) {
        user = userService.by(user.getId());
        model.addAttribute("user", user);
        return "payewm.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/toImage")
    public BufferedImage payToQRCode(long id, String comment, HttpServletRequest request)
            throws IOException, WriterException {
        StringBuilder builder = GlobalController.contextUrlBuilder(request);
        builder.append("/to/").append(id);
        if (comment != null) {
            builder.append("?comment=");
            builder.append(URLEncoder.encode(comment, "UTF-8"));
        }

        return qrCodeService.generateQRCode(builder.toString());
    }

    /**
     * 其他方向用户支付的页面
     *
     * @param openid  付款方微信openId
     * @param id      收款方用户id
     * @param comment 备注,用户可以随意更改
     * @param model   model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/to/{id}")
    public String payTo(@OpenId String openid, @PathVariable("id") long id, String comment, Model model) {
        model.addAttribute("user", userService.by(id));
        model.addAttribute("openId", openid);
        model.addAttribute("comment", comment);
        return "payment.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/to")
    public String submitPayTo(BigDecimal amount, String comment, String openid, long id, HttpServletRequest request
            , Model model) throws IOException, SignatureException {
        // 这里的comment 可能会是微信的昵称什么的 会导致支付报错;这里暂时先去掉
        PayToUserOrder order = orderService.newPayToOrder(openid, request, userService.by(id), amount, null);
//        return showPayCode(model, order);
        return "redirect:/order/" + order.getId();
    }

    /**
     * 开始付款--订单开始
     *
     * @param user
     * @param amount
     * @param comment
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/startOrder")
    public String start(@AuthenticationPrincipal User user, BigDecimal amount, String comment, Long card) {
        CashOrder order = orderService.newOrder(userService.by(user.getId()), amount, comment, card);
        return "redirect:/order/" + order.getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/orderCompleted/{id}"
            , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean orderStatus(@PathVariable("id") String id) {
        return orderService.isComplete(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/orderQRURL")
    public ResponseEntity<String> orderQRURL(String id, PayChannel channel) throws IOException, SignatureException {
        CashOrder order = orderService.getOne(id);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                .body(createPlatformOrder(order, channel));
    }

    /**
     * 打开这个付款二维码展示界面,开放显示
     * 这个页面应该会存在多个效果  比如微信平台的效果和非微信平台的效果
     * 在确认用户完成支付以后,会根据当前登录的不同身份获取不同的信息
     *
     * @param id    主订单id
     * @param model 数据
     * @return 将来的页面
     */
    @RequestMapping(method = RequestMethod.GET, value = "/order/{id}")
    public String orderInfo(@OpenId String openId, @PathVariable("id") String id, Model model) throws IOException
            , SignatureException {
        CashOrder order = orderService.getOne(id);
        if (orderService.isComplete(id)) {
            final String successURI = order.getSuccessURI();
            if (!StringUtils.isEmpty(successURI))
                return "redirect:" + successURI;
            // 看是不是我
            boolean isMe = order.getOwner().getOpenId().equals(openId);

            LocalDateTime time = order.getPlatformOrderSet().stream()
                    .filter(PlatformOrder::isFinish)
                    .map(PlatformOrder::getFinishTime)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            int type;
            if (order.isWithdrawalCompleted()) {
                type = 0;
            } else if (LocalDateTime.now().isBefore(time.plusMinutes(10))) {
                type = 1;
            } else
                type = 2;
            model.addAttribute("type", type);

            if (!isMe) {

                // current<f+10M
                // 如果已到账则显示已到账,10分钟内打款的 则提示正常的 否则显示  资金正在发向对方账户
                return "payOtherComplete.html";
            } else {
                model.addAttribute("order", order);
                // 我自己的订单 看下是否需要绑定银行卡
                model.addAttribute("cardRequired"
                        , order.getOwner().getCards() == null || order.getOwner().getCards().isEmpty());
                return "payMyComplete.html";
            }
//            return "completed.html";
        }

//        if (!order.getOwner().equals(user)) {
//            return "redirect:/";
//        }
        // 直接建立订单
//        return showPayCode(model, order);
        model.addAttribute("order", order);
        return "paycode.html";
    }

    private String createPlatformOrder(CashOrder order, PayChannel channel) throws IOException, SignatureException {
        if (channel == null)
            channel = PayChannel.weixin;
        PlatformOrder platformOrder = orderService.preparePay(order.getId(), channel);

        final ArbitrageChannel arbitrageChannel = applicationContext.getBean(platformOrder.channelClass());
        return arbitrageChannel.QRCodeImageFromOrder(platformOrder);
    }

    private String showPayCode(Model model, CashOrder order) throws IOException, SignatureException {
        model.addAttribute("qrUrl", createPlatformOrder(order, PayChannel.weixin));
        model.addAttribute("order", order);
        return "paycode.html";
    }


}
