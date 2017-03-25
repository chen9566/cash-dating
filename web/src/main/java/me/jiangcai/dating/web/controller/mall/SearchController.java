package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;

import static me.jiangcai.dating.web.controller.mall.IndexController.MallMode;

/**
 * @author CJ
 */
@RequestMapping("/mall")
@Controller
public class SearchController {

    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;

    @RequestMapping(method = RequestMethod.GET, value = {"/"})
    @Transactional(readOnly = true)
    public String home(HttpSession session, String order, Model model) {
        session.setAttribute(MallMode, true);

        // 寻找特卖商品
        List<FakeGoods> specialList = fakeGoodsRepository.findByDiscountIsNotNullAndEnableTrueAndSpecialTrue();
        model.addAttribute("specialList", specialList);


        return "/mall/index.html";
    }
}
