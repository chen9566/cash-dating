package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.service.sale.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
