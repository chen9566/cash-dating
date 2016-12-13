package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.support.RateConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class SystemServiceTest extends ServiceBaseTest {

    @Autowired
    private SystemService systemService;

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
            public BigDecimal bookProfileRate(SystemService systemService) {
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
        });
    }

    @Test
    public void currentRateConfig() {

        BigDecimal maxRate = new BigDecimal("0.0034");
        BigDecimal halfRate = maxRate.multiply(new BigDecimal("0.5"));
        BigDecimal bacheng = maxRate.multiply(new BigDecimal("0.2"));

        RateConfig config = systemService.currentRateConfig(new ProfitSplit() {
            @Override
            public BigDecimal bookProfileRate(SystemService systemService) {
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
        });
        assertThat(config.getAgentRate())
                .isEqualByComparingTo("0");
        assertThat(config.getGuideRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(maxRate);

        config = systemService.currentRateConfig(new ProfitSplit() {
            @Override
            public BigDecimal bookProfileRate(SystemService systemService) {
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
            public BigDecimal bookProfileRate(SystemService systemService) {
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
        });
        assertThat(config.getGuideRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(halfRate);

        config = systemService.currentRateConfig(new ProfitSplit() {
            @Override
            public BigDecimal bookProfileRate(SystemService systemService) {
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
        });
        assertThat(config.getAgentRate())
                .isEqualByComparingTo("0");
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(halfRate);

        // both

        config = systemService.currentRateConfig(new ProfitSplit() {
            @Override
            public BigDecimal bookProfileRate(SystemService systemService) {
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
        });
        assertThat(config.getPlatformRate())
                .isEqualByComparingTo(bacheng);

        // no!!
    }


}