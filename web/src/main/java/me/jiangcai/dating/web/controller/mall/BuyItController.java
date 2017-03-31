package me.jiangcai.dating.web.controller.mall;

import com.google.zxing.WriterException;
import me.jiangcai.dating.entity.PayOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.dating.service.sale.MallTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
 * @author CJ
 */
@Controller
@RequestMapping("/mall")
public class BuyItController {

    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private MallTradeService mallTradeService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private Environment environment;

    @RequestMapping(method = RequestMethod.GET, value = "/{goodsId}.goods")
    public String detail(@PathVariable("goodsId") long goodsId, Model model) {
        model.addAttribute("goods", fakeGoodsRepository.getOne(goodsId));
        return "/mall/details.html";
    }

    /**
     * 创建交易订单以及支付订单
     *
     * @return 支付订单号
     */
    @RequestMapping(method = RequestMethod.GET, value = "/createOrder/{goodsId}/{count}")
    @Transactional
    @ResponseBody
    public String createOrder(@AuthenticationPrincipal User user, @PathVariable("goodsId") long goodsId
            , @PathVariable("count") int count) {
        /// freeOrder/id
        CashGoods goods = mallGoodsService.findGoods(goodsId);
        CashTrade trade = mallGoodsService.createOrder(userService.by(user.getId()), goods, count);
        PayOrder payOrder = mallTradeService.createPayOrder(trade.getId(), null);
        return payOrder.getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/showOrder")
    public BufferedImage showOrder(String id) throws IOException, WriterException {
        return qrCodeService.generateQRCode(
                environment.getProperty("dating.url", "http://localhost") + "/freeOrder/"
                        + id);
    }
}
