package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.sale.MallTradeService;
import me.jiangcai.goods.trade.TradeStatus;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @Autowired
    private CashTradeRepository cashTradeRepository;

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

    // 订单列表
    @RequestMapping(method = RequestMethod.GET, value = "/myOrders")
    @Transactional(readOnly = true)
    public String orders(@AuthenticationPrincipal User user, Model model
            , @RequestParam(required = false) TradeStatus type) {
        // 至少需要知道有没有这个类型的
        model.addAttribute("type", type);
        model.addAttribute("hasData", cashTradeRepository.count(mallTradeService.tradeSpecification(user, type)) > 0);
        return "sale/myorder.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myOrdersData")
    @Transactional(readOnly = true)
    public String orderData(@AuthenticationPrincipal User user, Model model
            , @RequestParam(required = false) TradeStatus type, long offset) {
        model.addAttribute("type", type);
        model.addAttribute("offset", offset);
        model.addAttribute("list", cashTradeRepository.findAll(
                mallTradeService.tradeSpecification(user, type)
                , new Pageable() {
                    @Override
                    public int getPageNumber() {
                        return 0;
                    }

                    @Override
                    public int getPageSize() {
                        return 10;
                    }

                    @Override
                    public int getOffset() {
                        return (int) offset;
                    }

                    @Override
                    public Sort getSort() {
                        return new Sort(Sort.Direction.DESC, "createdTime");
                    }

                    @Override
                    public Pageable next() {
                        return null;
                    }

                    @Override
                    public Pageable previousOrFirst() {
                        return null;
                    }

                    @Override
                    public Pageable first() {
                        return null;
                    }

                    @Override
                    public boolean hasPrevious() {
                        return false;
                    }
                }).getContent());
        return "sale/myorder_data.html";
    }

    // 确认收货 /trade/456/confirm
    // TODO 如何自动收货？
    @RequestMapping(method = RequestMethod.PUT, value = "/trade/{id}/confirm")
    @ResponseBody
    public void makeConfirm(@AuthenticationPrincipal User user, @PathVariable("id") long id) {
        mallTradeService.confirmTrade(user, id);
    }

}
