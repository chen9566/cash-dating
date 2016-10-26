package me.jiangcai.dating;

import me.jiangcai.chanpay.event.AbstractTradeEvent;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, CoreConfig.class})
public abstract class ServiceBaseTest extends SpringWebTest {
    @Autowired
    protected UserService userService;
    @Autowired
    protected CardService cardService;
    @Autowired
    protected OrderService orderService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected SubBranchBankRepository subBranchBankRepository;
    @Autowired
    protected VerificationCodeService verificationCodeService;
    @Autowired
    protected ChanpayService chanpayService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected CashOrderRepository cashOrderRepository;

    protected SubBranchBank randomSubBranchBank() {
        return subBranchBankRepository.findAll().stream()
                .max(new RandomComparator())
                .orElse(null);
    }

    protected String randomBankCard() {
        return RandomStringUtils.randomNumeric(16);
    }

    /**
     * @return 建立新的用户
     */
    protected WeixinUserDetail createNewUser() {
        WeixinUserDetail detail = WeixinUserMocker.randomWeixinUserDetail();
        String mobile = randomMobile();
        verificationCodeService.sendCode(mobile, VerificationType.register);
        userService.registerMobile(null, detail.getOpenId(), mobile, "1234", null);
//        verificationCodeService.sendCode(mobile, Function.identity()); 现在不用发验证码了
        // 16
        String card = randomBankCard();
        cardService.addCard(detail.getOpenId(), detail.getNickname(), card
                , null, null, randomSubBranchBank().getCode());
        return detail;
    }

    /**
     * 改变订单时间
     *
     * @param order
     * @param time
     */
    protected void changeTime(CashOrder order, LocalDateTime time) {
        order.setStartTime(time);
        cashOrderRepository.save(order);
    }

    /**
     * 让这个订单完成支付
     *
     * @param order
     */
    protected void tradeSuccess(CashOrder order) throws IOException, SignatureException {
        order = cashOrderRepository.getOne(order.getId());
        PlatformOrder platformOrder = orderService.preparePay(order.getId(), PayChannel.weixin);

        TradeEvent tradeEvent = new TradeEvent(TradeStatus.TRADE_SUCCESS);
        mockEventInfo(tradeEvent);
        tradeEvent.setAmount(order.getAmount());
        tradeEvent.setSerialNumber(platformOrder.getId());

        chanpayService.tradeUpdate(tradeEvent);
        System.out.println("1");
    }

    private void mockEventInfo(AbstractTradeEvent tradeEvent) {
        tradeEvent.setTradeTime(LocalDateTime.now());
        tradeEvent.setPlatformOrderNo(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    /**
     * 使这个订单提现失败
     *
     * @param order  订单
     * @param status 转移状态
     * @param reason 原因
     */
    protected void withdrawalFailed(CashOrder order, WithdrawalStatus status, String reason) {
        order = cashOrderRepository.getOne(order.getId());
        PlatformWithdrawalOrder withdrawalOrder = order.getPlatformWithdrawalOrderSet().stream()
                .max((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .orElseThrow(IllegalStateException::new);

        WithdrawalEvent withdrawalEvent = new WithdrawalEvent(status);
        mockEventInfo(withdrawalEvent);
        withdrawalEvent.setAmount(order.getWithdrawalAmount());
        withdrawalEvent.setSerialNumber(withdrawalOrder.getId());
        if (reason != null)
            withdrawalEvent.setMessage(reason);

        chanpayService.withdrawalUpdate(withdrawalEvent);
        System.out.println("1");
    }

    /**
     * 让这个订单完成提现
     *
     * @param order
     */
    protected void withdrawalSuccess(CashOrder order) throws IOException, SignatureException {
        withdrawalFailed(order, WithdrawalStatus.WITHDRAWAL_SUCCESS, null);
    }

    public static class RandomComparator implements Comparator<Object> {
        static Random random = new Random();

        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }
}
