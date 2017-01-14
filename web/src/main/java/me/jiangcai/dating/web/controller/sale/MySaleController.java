package me.jiangcai.dating.web.controller.sale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
