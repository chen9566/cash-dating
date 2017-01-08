package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.dating.service.sale.MallTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 商城控制器
 * 所有商城都将以
 * /sale作为开头
 *
 * @author CJ
 */
@Controller
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private UserService userService;
    @Autowired
    private MallTradeService mallTradeService;

    @RequestMapping(method = RequestMethod.GET, value = {"/index", "/", ""})
    public String index(org.springframework.ui.Model model) {
        model.addAttribute("goodsList", mallGoodsService.saleGoods());
        return "sale/saleindex.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/goodsDetail")
    @Transactional(readOnly = true)
    public String detail(Model model, long id) {
        CashGoods goods = mallGoodsService.findGoods(id);
        model.addAttribute("goods", goods);
        if (goods.isTicketGoods()) {
            model.addAttribute("info", mallGoodsService.ticketInfo(goods));
            return "sale/carddetails.html";
        }
        throw new IllegalArgumentException("what about :" + goods);
    }

    // 建立订单
    @RequestMapping(method = RequestMethod.POST, value = "/createOrder")
    @ResponseBody
    @Transactional
    public long createOrder(@AuthenticationPrincipal User user, @RequestBody Map<String, Number> data) {
        CashGoods goods = mallGoodsService.findGoods(data.get("id").longValue());
        CashTrade trade = mallGoodsService.createOrder(userService.by(user.getId()), goods, data.get("count").intValue());
        return trade.getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/showOrder")
    public String showOrder(long id, Model model) {
        model.addAttribute("trade", mallTradeService.trade(id));
        return "sale/cardplay.html";
    }
}
