package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

/**
 * @author CJ
 */
public class ChanpayServiceTest extends ServiceBaseTest {

    @Autowired
    private ChanpayService chanpayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private UserService userService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SubBranchBankRepository subBranchBankRepository;

    protected String randomBankCard() {
        return RandomStringUtils.randomNumeric(16);
    }

    protected SubBranchBank randomSubBranchBank() {
        return subBranchBankRepository.findAll().stream()
                .max(new RandomComparator())
                .orElse(null);
    }

    @Test
    public void xx() throws IOException, SignatureException {

        WeixinUserDetail detail = WeixinUserMocker.randomWeixinUserDetail();
        String mobile = randomMobile();
        verificationCodeService.sendCode(mobile, VerificationType.register);
        User user = userService.registerMobile(null, detail.getOpenId(), mobile, "1234", null);
//        verificationCodeService.sendCode(mobile, Function.identity()); 现在不用发验证码了
        // 16
        String card = randomBankCard();
        userService.addCard(detail.getOpenId(), detail.getNickname(), card
                , null, null, randomSubBranchBank().getCode());


        CashOrder cashOrder = orderService.newOrder(user, new BigDecimal("0.1"), UUID.randomUUID().toString(), null);
        chanpayService.createOrder(cashOrder);
        chanpayService.withdrawalOrder(cashOrder);
    }

    private static class RandomComparator implements Comparator<Object> {
        static Random random = new Random();

        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }

}