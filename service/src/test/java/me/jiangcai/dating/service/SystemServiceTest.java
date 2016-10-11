package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.support.RateConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class SystemServiceTest extends ServiceBaseTest {

    @Autowired
    private SystemService systemService;

    @Test(expected = InternalError.class)
    public void badCurrentRateConfig() {
        RateConfig config = systemService.currentRateConfig(new ProfitSplit() {
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