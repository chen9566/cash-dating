package me.jiangcai.dating;

import me.jiangcai.chanpay.event.AbstractTradeEvent;
import me.jiangcai.chanpay.event.TradeEvent;
import me.jiangcai.chanpay.event.WithdrawalEvent;
import me.jiangcai.chanpay.model.TradeStatus;
import me.jiangcai.chanpay.model.WithdrawalStatus;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.PlatformWithdrawalOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.channel.ChroneOrder;
import me.jiangcai.dating.model.PayChannel;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.repository.UserOrderRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.xalan.xsltc.compiler.util.InternalError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected UserOrderRepository userOrderRepository;
    @Autowired
    protected ResourceService resourceService;
    @Autowired
    protected ApplicationContext applicationContext;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public SubBranchBank randomSubBranchBank() {
        return subBranchBankRepository.findAll().stream()
                .max(new RandomComparator())
                .orElse(null);
    }

    /**
     * A->B
     * A->C
     * C->D
     * C->E
     * E->F
     * E->G
     *
     * @return 创建一个经典的用户关系图
     */
    protected Map<String, User> createClassicsUsers() {
        User a = userService.byOpenId(createNewUser().getOpenId());
        User b = userService.byOpenId(createNewUser(a).getOpenId());
        User c = userService.byOpenId(createNewUser(a).getOpenId());
        User d = userService.byOpenId(createNewUser(c).getOpenId());
        User e = userService.byOpenId(createNewUser(c).getOpenId());
        User f = userService.byOpenId(createNewUser(e).getOpenId());
        User g = userService.byOpenId(createNewUser(e).getOpenId());
        Map<String, User> map = new HashMap<>();
        map.put("a", a);
        map.put("b", b);
        map.put("c", c);
        map.put("d", d);
        map.put("e", e);
        map.put("f", f);
        map.put("g", g);
        return map;
    }

    public String randomBankCard() {
        return RandomStringUtils.randomNumeric(16);
    }

    /**
     * @return 建立新的用户
     */
    protected WeixinUserDetail createNewUser() {
        return createNewUser(null);
    }

    /**
     * @return 建立新的用户
     */
    protected WeixinUserDetail createNewUser(User guide) {
        WeixinUserDetail detail = WeixinUserMocker.randomWeixinUserDetail();
        String mobile = randomMobile();
        verificationCodeService.sendCode(mobile, VerificationType.register);
        User user = userService.registerMobile(null, detail.getOpenId(), mobile, "1234", guide == null ? null : guide.getInviteCode());
        user.setNickname(detail.getNickname());
        userRepository.save(user);
//        verificationCodeService.sendCode(mobile, Function.identity()); 现在不用发验证码了
        // 16
        String card = randomBankCard();
        cardService.addCard(detail.getOpenId(), detail.getNickname(), null, card
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
    public void tradeSuccess(CashOrder order) throws IOException, SignatureException {
        order = cashOrderRepository.getOne(order.getId());
        PlatformOrder platformOrder = orderService.preparePay(order.getId(), PayChannel.weixin);

        if (platformOrder instanceof ChanpayOrder) {
            TradeEvent tradeEvent = new TradeEvent(TradeStatus.TRADE_SUCCESS);
            mockEventInfo(tradeEvent);
            tradeEvent.setAmount(order.getAmount());
            tradeEvent.setSerialNumber(platformOrder.getId());
            applicationEventPublisher.publishEvent(tradeEvent);
        } else if (platformOrder instanceof ChroneOrder) {
            // 访问它的URL即可
            try {
                mockMvc.perform(get(platformOrder.getUrl()))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new InternalError(e.getMessage());
            }
        } else
            throw new NoSuchMethodError("no support for " + platformOrder);
//        chanpayService.tradeUpdate(tradeEvent);
//        System.out.println("1");
    }

    private void mockEventInfo(AbstractTradeEvent tradeEvent) {
        tradeEvent.setTradeTime(LocalDateTime.now());
        tradeEvent.setPlatformOrderNo(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    /**
     * 让一个可提现的订单拥有最终结果
     *
     * @param order   订单
     * @param success 是否成功
     * @param reason  原因
     */
    protected void withdrawalResult(UserOrder order, boolean success, String reason) {
        order = userOrderRepository.getOne(order.getId());
        // 如果是提现订单 那就得考虑关于提现的事儿了
        if (!order.isArbitrage()) {
            // TODO 提现服务渠道以后也将建立
            PlatformWithdrawalOrder withdrawalOrder = order.getPlatformWithdrawalOrderSet().stream()
                    .max((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                    .orElseThrow(IllegalStateException::new);

            WithdrawalEvent withdrawalEvent = new WithdrawalEvent(success ? WithdrawalStatus.WITHDRAWAL_SUCCESS : WithdrawalStatus.WITHDRAWAL_FAIL);
            mockEventInfo(withdrawalEvent);
            withdrawalEvent.setAmount(order.getWithdrawalAmount());
            withdrawalEvent.setSerialNumber(withdrawalOrder.getId());
            if (reason != null)
                withdrawalEvent.setMessage(reason);

            applicationEventPublisher.publishEvent(withdrawalEvent);
//            chanpayService.withdrawalUpdate(withdrawalEvent);
        } else {
            // 获取付款订单
            CashOrder cashOrder = (CashOrder) order;
            PlatformOrder platformOrder = cashOrder.getPlatformOrderSet().stream()
                    .filter(PlatformOrder::isFinish)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("没有找到成功的付款单,无法模拟支付结果。"));

            final ArbitrageChannel bean = applicationContext.getBean(platformOrder.channelClass());
            bean.mockArbitrageResult(cashOrder, success, reason);
        }
//        System.out.println("1");
    }

    /**
     * 让这个订单完成提现
     *
     * @param order
     */
    protected void withdrawalSuccess(UserOrder order) throws IOException, SignatureException {
        withdrawalResult(order, true, null);
    }

    /**
     * 创建一个已完成的提现订单
     *
     * @param user
     * @param amount
     * @return
     * @throws IOException
     * @throws SignatureException
     */
    protected WithdrawOrder makeFinishWithdrawOrder(User user, BigDecimal amount) throws IOException, SignatureException {
        WithdrawOrder order = orderService.newWithdrawOrder(user, amount
                , (user.getCards() == null || user.getCards().isEmpty()) ? null : user.getCards().get(0).getId());
        withdrawalSuccess(order);
        return order;
    }

    /**
     * 构建一个完成的订单
     *
     * @param user    owner
     * @param amount  金额
     * @param comment 备注
     * @return 订单
     */
    protected CashOrder makeFinishCashOrder(User user, BigDecimal amount, String comment) throws IOException, SignatureException {
        CashOrder order = orderService.newOrder(user, amount, comment
                , (user.getCards() == null || user.getCards().isEmpty()) ? null : user.getCards().get(0).getId());
        tradeSuccess(order);
        withdrawalSuccess(order);
        return order;
    }

    public MockMvc mockMVC() {
        return mockMvc;
    }

    /**
     * 100-100000
     *
     * @return 随机的订单金额
     */
    protected BigDecimal randomOrderAmount() {
        return BigDecimal.valueOf(100 + random.nextInt(100000 - 100));
    }


    /**
     * @return 随机生成的图片资源路径
     */
    protected String randomImageResourcePath() throws IOException {
        String name = "tmp/" + UUID.randomUUID().toString() + ".png";
        resourceService.uploadResource(name, applicationContext.getResource("/images/1.png").getInputStream());
        return name;
    }

    /**
     * 添加余额
     *
     * @param openId 用户
     * @param amount 金额
     */
    protected void addUserBalance(String openId, BigDecimal amount) {
        User user = userService.byOpenId(openId);
        user.setSettlementBalance(user.getSettlementBalance().add(amount));
        userRepository.save(user);
    }

    /**
     * @return 随机身份证
     */
    protected String randomPeopleId() {
        // 8 + 4
        return "33032419831021"
                + org.apache.commons.lang.RandomStringUtils.randomNumeric(4);
    }

    @Override
    protected String randomMobile() {
        return "186" + org.apache.commons.lang.RandomStringUtils.randomNumeric(8);
    }


    public static class RandomComparator implements Comparator<Object> {
        static Random random = new Random();

        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }
}
