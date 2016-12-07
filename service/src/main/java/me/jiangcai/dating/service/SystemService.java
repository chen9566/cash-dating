package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.entity.support.RateConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统级别
 *
 * @author CJ
 */
public interface SystemService {

    String DefaultPublicAccountName = "默认微信公众号";

    /**
     * 自动调度
     */
    @PostConstruct
    @Transactional
    void init() throws UnsupportedEncodingException;

    /**
     * @param profitSplit 利润分配者
     * @return 当前的配置
     */
    @Transactional(readOnly = true)
    RateConfig currentRateConfig(ProfitSplit profitSplit);

    /**
     * @param profitSplit 相关
     * @return 账面手续费
     */
    BigDecimal systemBookRate(ProfitSplit profitSplit);

    /**
     * 获取配置值
     *
     * @param key          key
     * @param exceptedType 期待的类型
     * @param defaultValue 默认数据
     * @param <T>          范型
     * @return 默认数据或者当前值
     */
    @Transactional(readOnly = true)
    <T> T getSystemString(String key, Class<T> exceptedType, T defaultValue);

    /**
     * 更新配置,并不会影响已下的订单
     *
     * @param rateConfig
     */
    @Transactional
    void updateRateConfig(RateConfig rateConfig);

    @Transactional
    void updateSystemString(String key, String value);

    @Transactional
    void updateSystemString(String key, LocalDateTime value);

    @Transactional
    void updateSystemString(String key, BigDecimal decimal);

    /**
     * @return 公众号名称
     */
    String getPublicAccountName();

    /**
     * @return 办卡的URL
     */
    String getApplyCardUrl();

    /**
     * @return 项目贷款授信年限
     */
    default int getProjectLoanCreditLimit() {
        return getSystemString("dating.project.loan.limit", BigDecimal.class, new BigDecimal(2)).intValue();
    }

    default void updateProjectLoanCreditLimit(int years) {
        updateSystemString("dating.project.loan.limit", new BigDecimal(years));
    }

    /**
     * @return 推荐年化利率(项目贷款)
     */
    default BigDecimal getProjectLoanYearRate() {
        return getSystemString("dating.project.loan.yearRate", BigDecimal.class, new BigDecimal("0.1"));
    }

    default void updateProjectLoanYearRate(BigDecimal rate) {
        updateSystemString("dating.project.loan.yearRate", rate);
    }

    // 这里是设置一个比例,没批准一个 下次就丢失一个
    @Transactional(readOnly = true)
    default BigDecimal[] getProjectLoanTermRates(String[] terms) {
        BigDecimal[] result = new BigDecimal[terms.length];
        for (int i = 0; i < terms.length; i++) {
            result[i] = getSystemString("dating.project.loan.term" + terms[i], BigDecimal.class, i == 0 ? BigDecimal.ONE : BigDecimal.ZERO);
        }
        return result;
    }

    @Transactional
    default void updateProjectLoanTermRates(String[] terms, BigDecimal[] rates) {
        for (int i = 0; i < terms.length; i++) {
            //没有设置那就是0
            BigDecimal target;
            if (rates.length > i) {
                target = rates[i];
            } else
                target = BigDecimal.ZERO;
            updateSystemString("dating.project.loan.term" + terms[i], target);
        }
    }


//    double
}
