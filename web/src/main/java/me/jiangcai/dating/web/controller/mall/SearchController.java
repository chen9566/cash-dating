package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.criteria.Predicate;
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


    @RequestMapping(method = RequestMethod.GET, value = {"/search"})
    @Transactional(readOnly = true)
    public String search(Model model, String order, String search, String category, Boolean hot, Boolean special) {
        model.addAttribute("order", order);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("hot", hot);
        model.addAttribute("special", special);

        Sort sort = getOrders(order);
        model.addAttribute("goodsList", fakeGoodsRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.isTrue(root.get("enable"));
            if (!StringUtils.isEmpty(search)) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + search + "%"));
            }

            if (!StringUtils.isEmpty(category)) {
                predicate = cb.and(predicate, cb.equal(root.get("fakeCategory"), FakeCategory.valueOf(category)));
            }

            if (hot != null && hot) {
                predicate = cb.and(predicate, cb.isTrue(root.get("hot")));
            }

            if (special != null && special) {
                predicate = cb.and(predicate, cb.isTrue(root.get("special")));
            }

            return predicate;
        }, sort));

        return "/mall/search.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/", "/index"})
    @Transactional(readOnly = true)
    public String home(HttpSession session, String order, Model model) {
        session.setAttribute(MallMode, true);

        model.addAttribute("order", order);
        // 寻找特卖商品
        List<FakeGoods> specialList = fakeGoodsRepository.findByDiscountIsNotNullAndEnableTrueAndSpecialTrue();
        model.addAttribute("specialList", specialList);
        Sort sort = getOrders(order);


        model.addAttribute("goodsList", fakeGoodsRepository.findBySpecialFalseAndEnableTrue(sort));

        return "/mall/index.html";
    }

    private Sort getOrders(String order) {
        // 排序
        Sort sort;
        if ("cheap".equalsIgnoreCase(order)) {
            sort = new Sort(Sort.Direction.ASC, "price");
        } else if ("expensive".equalsIgnoreCase(order)) {
            sort = new Sort(Sort.Direction.DESC, "price");
        } else {
            sort = new Sort(Sort.Direction.DESC, "weight").and(new Sort(Sort.Direction.DESC, "createTime"));
        }
        return sort;
    }
}
