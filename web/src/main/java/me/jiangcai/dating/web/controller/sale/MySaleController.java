package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.dating.service.sale.MallTradeService;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跟「我的」相关的
 * /my 我的
 * /myOrders 所有订单 type=ordered 待付款 type=paid 待发货 type=sent 待确认 type=confirmed 完成
 * /myOrder?id 某一订单详情
 *
 * @author CJ
 */
@Controller
@RequestMapping("/sale")
public class MySaleController {

    @Autowired
    private MallTradeService mallTradeService;

    @RequestMapping(method = RequestMethod.GET, value = "/my")
    public String index(@AuthenticationPrincipal User user, Model model) {
        List<TicketCode> ticketCodeList = mallTradeService.ticketCodes(user);

        Map<Boolean, List<TicketCode>> groupedCodes =
                ticketCodeList.stream().collect(Collectors.partitioningBy(TicketCode::isUserFlag));

        model.addAttribute("usedCodes", groupedCodes.get(true));
        model.addAttribute("usableCodes", groupedCodes.get(false));

        return "sale/my.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myTicket")
    public String myTicket(@AuthenticationPrincipal User user, Model model, String code) throws DecoderException
            , UnsupportedEncodingException {
        final TicketCode value = mallTradeService.ticketCode(TicketCodePK.valueOf(code), user);
        model.addAttribute("code", value);
        if (value.isUserFlag())
            return "sale/mycouponsclose.html";
        return "sale/mycoupons.html";
    }

}
