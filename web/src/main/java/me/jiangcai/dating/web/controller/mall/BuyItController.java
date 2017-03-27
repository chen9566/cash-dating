package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/mall")
public class BuyItController {

    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/{goodsId}")
    public String detail(@PathVariable("goodsId") long goodsId, Model model) {
        model.addAttribute("goods", fakeGoodsRepository.getOne(goodsId));
        return "/mall/details.html";
    }
}
