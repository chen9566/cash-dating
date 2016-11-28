package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.service.OrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 入口 /manage/order
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Order_Value + "')")
@Controller
public class ManageOrderController {

    private static final Log log = LogFactory.getLog(ManageOrderController.class);

    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/order/withdrawal/{id}")
    public ResponseEntity<?> withdrawal(@PathVariable("id") String id) {
        try {
            orderService.withdrawalWithCard(id, null);
            return ResponseEntity.ok().body("");
        } catch (Throwable ex) {
            log.debug("mandalay", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/manage/order")
    public String index() {
        return "manage/order.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/manage/order")
    public String query(Model model, String search) {
        if (StringUtils.isEmpty(search)) {
            model.addAttribute("message", "至少你得输入些什么,内容可以随意,比如订单号,用户昵称");
            return "manage/order.html";
        }

        //先不分页了
        List<UserOrder> orderList = orderService.queryUserOrders(search);
        model.addAttribute("orders", orderList);
        model.addAttribute("search", search);

        return "manage/userOrders.html";
    }


}
