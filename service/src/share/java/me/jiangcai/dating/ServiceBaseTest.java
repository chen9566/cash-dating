package me.jiangcai.dating;

import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.wx.model.WeixinUserDetail;
import me.jiangcai.wx.test.WeixinUserMocker;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Comparator;
import java.util.Random;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, CoreConfig.class})
public abstract class ServiceBaseTest extends SpringWebTest {
    @Autowired
    protected UserService userService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SubBranchBankRepository subBranchBankRepository;
    @Autowired
    private VerificationCodeService verificationCodeService;

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
        userService.addCard(detail.getOpenId(), detail.getNickname(), card
                , null, null, randomSubBranchBank().getCode());
        return detail;
    }

    public static class RandomComparator implements Comparator<Object> {
        static Random random = new Random();

        @Override
        public int compare(Object o1, Object o2) {
            return random.nextInt();
        }
    }
}
