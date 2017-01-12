package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.chanpay.data.trade.QueryTrade;
import me.jiangcai.chanpay.data.trade.QueryTradeResult;
import me.jiangcai.chanpay.event.AbstractTradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.chanpay.exception.ServiceException;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.model.TradeType;
import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.chanpay.service.TransactionService;
import me.jiangcai.chanpay.service.impl.QueryTradeHandler;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.repository.UserOrderRepository;
import me.jiangcai.dating.service.OrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;
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
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private OrderService orderService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ApplicationContext applicationContext;

    private void checkOrder(UserOrder order) throws IOException, SignatureException {
        if (order instanceof CashOrder) {
            if (!((CashOrder) order).isCompleted() && ((CashOrder) order).getPlatformOrderSet() != null) {
                // 这里需要检查相关的支付订单
                for (PlatformOrder platformOrder : ((CashOrder) order).getPlatformOrderSet()) {
                    if (platformOrder.isFinish())
                        continue;
                    ArbitrageChannel channel = applicationContext.getBean(platformOrder.arbitrageChannelClass());
                    if (channel.checkPayResult(platformOrder))
                        break;
                }
            }
        }
        // 检查支付订单 现在系统存在一个问题需要顺手修复 及时已支付成功并不影响继续成功
        if (order.getPlatformWithdrawalOrderSet() != null)
            for (PlatformWithdrawalOrder withdrawalOrder : order.getPlatformWithdrawalOrderSet()) {
                if (withdrawalOrder.isFinish())
                    continue;
                QueryTrade queryTrade = new QueryTrade();
                queryTrade.setSerialNumber(withdrawalOrder.getId());
                queryTrade.setType(TradeType.WITHDRAWAL);
// ILLEGAL_OUTER_TRADE_NO 这个异常可以原谅
                try {
                    QueryTradeResult result = transactionService.execute(queryTrade, new QueryTradeHandler());
                    WithdrawalEvent withdrawalEvent;
                    if (result.getStatus() == TradeStatus.success) {
                        withdrawalEvent = new WithdrawalEvent(WithdrawalStatus.WITHDRAWAL_SUCCESS);
                    } else if (result.getStatus() == TradeStatus.failed) {
                        withdrawalEvent = new WithdrawalEvent(WithdrawalStatus.WITHDRAWAL_FAIL);
                    } else if (result.getStatus() == TradeStatus.submitted) {
                        withdrawalEvent = new WithdrawalEvent(WithdrawalStatus.WITHDRAWAL_SUBMITTED);
                    } else
                        throw new IllegalStateException("unknown status:" + result.getStatus());

                    commitEventWithQueryResult(result, withdrawalEvent);
                } catch (ServiceException exception) {
                    if (exception.getCode().equals("ILLEGAL_OUTER_TRADE_NO")) {
                        //发布失败事件
                        WithdrawalEvent withdrawalEvent = new WithdrawalEvent(WithdrawalStatus.WITHDRAWAL_FAIL);
                        withdrawalEvent.setTradeTime(LocalDateTime.now());
                        withdrawalEvent.setAmount(order.getWithdrawalAmount());
                        withdrawalEvent.setSerialNumber(withdrawalOrder.getId());
                        withdrawalEvent.setMessage("查询未找到结果");
                        applicationEventPublisher.publishEvent(withdrawalEvent);
                        continue;
                    }
                    // 无所谓的异常 就假装不知道好了
                    log.debug("Do Not Care ServiceException", exception);
//                    throw exception;
                }
            }
    }

    private void commitEventWithQueryResult(QueryTradeResult result, AbstractTradeEvent tradeEvent) {
        tradeEvent.setPlatformOrderNo(result.getChanPayNumber());
        tradeEvent.setSerialNumber(result.getSerialNumber());
        tradeEvent.setAmount(result.getAmount());
        tradeEvent.setTradeTime(result.getTime());
//        tradeEvent.setMessage(result.get);
        applicationEventPublisher.publishEvent(tradeEvent);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/order/checkOrder")
    public ResponseEntity<?> checkOrder(@RequestBody String id) {
        try {
            checkOrder(userOrderRepository.getOne(id));
            return ResponseEntity.ok().body("");
        } catch (Throwable ex) {
            log.debug("checkSearch", ex);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/order/checkSearch")
    public ResponseEntity<?> checkSearch(@RequestBody String search) {
        List<UserOrder> orderList = orderService.queryUserOrders(search);
        try {
            for (UserOrder order : orderList) {
                checkOrder(order);
            }
            return ResponseEntity.ok().body("");
        } catch (Throwable ex) {
            log.debug("checkSearch", ex);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/order/withdrawal/{id}")
    public ResponseEntity<?> withdrawal(@PathVariable("id") String id) {
        try {
            orderService.withdrawalWithCard(id, null);
            return ResponseEntity.ok().body("");
        } catch (Throwable ex) {
            log.debug("mandalay", ex);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body(ex.getMessage());
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
