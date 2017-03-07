package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.entity.support.RateConfig;
import me.jiangcai.dating.model.InviteLevel;
import me.jiangcai.dating.repository.LoanRequestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class SystemServiceTest extends ServiceBaseTest {

    @Autowired
    private SystemService systemService;
    @Autowired
    private WealthService wealthService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Test
    public void wealth() {
        boolean pay123 = random.nextBoolean();
        systemService.updateEnablePay123(pay123);
        assertThat(systemService.isEnablePay123())
                .isEqualTo(pay123);

        systemService.updateEnablePay123(false);


        int year = Math.abs(random.nextInt());
        systemService.updateProjectLoanCreditLimit(year);
        assertThat(systemService.getProjectLoanCreditLimit())
                .isEqualTo(year);
        /////

        // 新建一个term
        int[] terms = randomTerms();
        systemService.updateProjectLoanTermsStyle(terms);
        assertThat(systemService.getProjectLoanTermsStyle())
                .isEqualTo(terms);

        for (int t : terms) {
            BigDecimal decimal = new BigDecimal(Math.abs(random.nextDouble())).setScale(5, BigDecimal.ROUND_HALF_UP);
            systemService.updateProjectLoanYearRate(t, decimal);
            assertThat(systemService.getProjectLoanYearRate(t))
                    .isEqualTo(decimal);
            // count
            int count = 1 + random.nextInt(100);
            systemService.updateProjectLoanCountRate(t, count);
            assertThat(systemService.getProjectLoanCountRate(t))
                    .isEqualTo(count);
        }

        // 获取下一个贷款的周期
        assertThat(wealthService.nextProjectLoanTerm())
                .isEqualTo(terms[0]);
        // level 0 count
        int level0Count = systemService.getProjectLoanCountRate(terms[0]);
        List<LoanRequest> requestList = new ArrayList<>();
        while (level0Count-- > 0) {
            //
            LoanRequest request = new LoanRequest();
            request.setProcessStatus(LoanRequestStatus.contract);
            requestList.add(loanRequestRepository.save(request));
        }

        try {
            assertThat(wealthService.nextProjectLoanTerm())
                    .isEqualTo(terms[1]);
        } finally {
            loanRequestRepository.delete(requestList);
        }

    }

    private int[] randomTerms() {
        int count = 3 + random.nextInt(3);
        int[] data = new int[count];
        for (int i = 0; i < count; i++) {
            int value = 0;
            while (value == 0 || existing(value, data, i))
                value = 30 + random.nextInt(999);
            data[i] = value;
        }
        return data;
    }

    private boolean existing(int value, int[] data, int index) {
        for (int i = 0; i < index; i++) {
            if (data[i] == value)
                return true;
        }
        return false;
    }

    /**
     * 任意用户邀请了5个有效用户之后 即可获得「优惠手续费率」
     */
    @Test
    public void rate() throws IOException, SignatureException {
        String root = createNewUser().getOpenId();
        assertThat(makeFinishCashOrder(userService.byOpenId(root), randomOrderAmount(), null).getThatRateConfig().getBookRate())
                .isEqualByComparingTo(systemService.systemDefaultRate());
        //来5个哥们
        String user1 = createNewUser(userService.byOpenId(root)).getOpenId();
        String user2 = createNewUser(userService.byOpenId(root)).getOpenId();
        String user3 = createNewUser(userService.byOpenId(root)).getOpenId();
        String user4 = createNewUser(userService.byOpenId(root)).getOpenId();
        String user5 = createNewUser(userService.byOpenId(root)).getOpenId();

        //依然还是原来的
        assertThat(makeFinishCashOrder(userService.byOpenId(root), randomOrderAmount(), null).getThatRateConfig().getBookRate())
                .isEqualByComparingTo(systemService.systemDefaultRate());
        makeFinishCashOrder(userService.byOpenId(user1), randomOrderAmount(), null);
        makeFinishCashOrder(userService.byOpenId(user2), randomOrderAmount(), null);
        makeFinishCashOrder(userService.byOpenId(user3), randomOrderAmount(), null);
        makeFinishCashOrder(userService.byOpenId(user4), randomOrderAmount(), null);
        assertThat(makeFinishCashOrder(userService.byOpenId(root), randomOrderAmount(), null).getThatRateConfig().getBookRate())
                .isEqualByComparingTo(systemService.systemDefaultRate());
        makeFinishCashOrder(userService.byOpenId(user4), randomOrderAmount(), null);
        assertThat(makeFinishCashOrder(userService.byOpenId(root), randomOrderAmount(), null).getThatRateConfig().getBookRate())
                .isEqualByComparingTo(systemService.systemDefaultRate());

        makeFinishCashOrder(userService.byOpenId(user5), randomOrderAmount(), null);
        assertThat(makeFinishCashOrder(userService.byOpenId(root), randomOrderAmount(), null).getThatRateConfig().getBookRate())
                .isEqualByComparingTo(systemService.systemPreferentialRate());
    }

    @Test(expected = InternalError.class)
    public void badCurrentRateConfig() {
        RateConfig config = systemService.currentRateConfig(new ProfitSplit() {
            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return 0.5;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return 0.5;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
    }

    @Test
    public void currentRateConfig() {

        BigDecimal maxRate = new BigDecimal("0.0034");
        BigDecimal halfRate = maxRate.multiply(new BigDecimal("0.5"));
        BigDecimal bacheng = maxRate.multiply(new BigDecimal("0.2"));

        RateConfig config = systemService.currentRateConfig(new ProfitSplit() {

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return Double.NaN;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return Double.NaN;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
        assertThat(config.getAgentRate())
                .isEqualByComparingTo("0");
        assertThat(config.getGuideRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(maxRate);

        config = systemService.currentRateConfig(new ProfitSplit() {

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return 0;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return 0;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
        assertThat(config.getAgentRate())
                .isEqualByComparingTo("0");
        assertThat(config.getGuideRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(maxRate);

        //

        config = systemService.currentRateConfig(new ProfitSplit() {

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return 0.5;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return 0;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
        assertThat(config.getGuideRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(halfRate);

        config = systemService.currentRateConfig(new ProfitSplit() {

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return 0;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return 0.5;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
        assertThat(config.getAgentRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(halfRate);

        // both

        config = systemService.currentRateConfig(new ProfitSplit() {

            @Override
            public InviteLevel inviteLevel(UserService userService) {
                return null;
            }

            @Override
            public BigDecimal bookProfileRate(SystemService systemService, UserService userService) {
                return null;
            }

            @Override
            public double agentProfileRate(SystemService systemService) {
                return 0.4;
            }

            @Override
            public double guideProfileRate(SystemService systemService) {
                return 0.4;
            }

            @Override
            public BigDecimal guideRate(UserService userService) {
                return null;
            }
        });
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(bacheng);

        // no!!
    }


}