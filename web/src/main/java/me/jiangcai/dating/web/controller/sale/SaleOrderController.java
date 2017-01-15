package me.jiangcai.dating.web.controller.sale;

import com.google.zxing.WriterException;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketTrade;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.sale.MallTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 对订单进行操作
 *
 * @author CJ
 */
@Controller
@RequestMapping("/sale")
public class SaleOrderController {

    @Autowired
    private MallTradeService mallTradeService;
    @Autowired
    private QRCodeService qrCodeService;

    @RequestMapping(method = RequestMethod.GET, value = "/myOrder")
    @Transactional(readOnly = true)
    public String order(@AuthenticationPrincipal User user, Model model, long id) {
        CashTrade trade = mallTradeService.trade(id);
// 不是你的 看个球
        if (!trade.getUser().equals(user))
            throw new AccessDeniedException("");

        // 尚未支付？
        if (!trade.isPaidSuccess()) {
            // 去确认订单？ 应该不是 而是直接去支付
            return "redirect:/sale/showOrder?id=" + id;
        }

        model.addAttribute("trade", trade);
        if (trade instanceof TicketTrade)
            return "sale/cardplayfinish.html";
        throw new NoSuchMethodError("Not Support " + trade);
    }

    // 允许查看一个 二维码
    @RequestMapping(method = RequestMethod.GET, produces = "image/*", value = "/ticket/{code}")
    public BufferedImage ticketCode(@AuthenticationPrincipal User user, @PathVariable("code") String code)
            throws IOException, WriterException {
        TicketCode ticketCode = mallTradeService.ticketCode(code, user);
        return qrCodeService.generateQRCode(ticketCode.getCode());
    }

    // 使用了
    @RequestMapping(method = RequestMethod.PUT, value = "/ticket/{code}")
    @Transactional
    @ResponseBody
    public void setUserFlag(@AuthenticationPrincipal User user, @PathVariable("code") String code) {
        TicketCode ticketCode = mallTradeService.ticketCode(code, user);
        if (!ticketCode.isUserFlag())
            ticketCode.setUserFlag(true);
    }

}
