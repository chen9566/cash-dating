package me.jiangcai.dating.service;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.channel.PayChannel;
import me.jiangcai.dating.entity.support.RateConfig;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.service.impl.SystemServiceImpl;
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
     * @param openId 检测openId
     * @param number 要求数量
     * @return 是否已邀请足够数量的有效用户
     */
    @Transactional(readOnly = true)
    boolean hasInviteValidUser(String openId, int number);

    /**
     * @param channel 支付方式
     * @return 套现渠道
     */
    ArbitrageChannel arbitrageChannel(PayMethod channel);

    /**
     * @param method 方式
     * @return 支付渠道
     */
    PayChannel payChannel(PayMethod method);

    /**
     * @return 系统提供的优惠手续费率
     */
    BigDecimal systemPreferentialRate();

    /**
     * @return 系统提供的默认手续费率
     */
    BigDecimal systemDefaultRate();

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

    /**
     * 可在脚本环境中调用
     *
     * @param years 项目贷款授信年限
     */
    default void updateProjectLoanCreditLimit(int years) {
        updateSystemString("dating.project.loan.limit", new BigDecimal(years));
    }

    /**
     * @return 贷款期限周期规格 单位：天
     */
    default int[] getProjectLoanTermsStyle() {
        String terms = getSystemString("dating.project.loan.terms.style", String.class, "30,90,180");
        String[] termStrings = terms.split(",");
        int[] termInt = new int[termStrings.length];
        for (int i = 0; i < termStrings.length; i++) {
            termInt[i] = Integer.parseInt(termStrings[i]);
        }
        return termInt;
    }

    /**
     * 更新
     *
     * @param terms 贷款期限周期规格 单位：天
     */
    default void updateProjectLoanTermsStyle(int... terms) {
        String[] termStrings = new String[terms.length];
        for (int i = 0; i < terms.length; i++) {
            termStrings[i] = String.valueOf(terms[i]);
        }
        updateSystemString("dating.project.loan.terms.style", String.join(",", (CharSequence[]) termStrings));
    }

    /**
     * @param term 贷款期限周期规格 单位：天
     * @return 推荐年化利率(项目贷款)
     */
    default BigDecimal getProjectLoanYearRate(int term) {
        BigDecimal rate = getSystemString("dating.project.loan.yearRate." + term, BigDecimal.class, null);
        if (rate == null) {
            switch (term) {
                case 30:
                    rate = new BigDecimal("0.07");
                    break;
                case 90:
                    rate = new BigDecimal("0.09");
                    break;
                default:
                    rate = new BigDecimal("0.1");
            }
        }
        return rate;
    }

    /**
     * 更新推荐年化利率
     *
     * @param term 贷款期限周期规格 单位：天
     * @param rate 推荐年化利率(项目贷款)
     */
    default void updateProjectLoanYearRate(int term, BigDecimal rate) {
        updateSystemString("dating.project.loan.yearRate." + term, rate);
    }

    /**
     * @param term 贷款期限周期规格 单位：天
     * @return 批准数量比例 默认20
     */
    default int getProjectLoanCountRate(int term) {
        BigDecimal rate = getSystemString("dating.project.loan.countRate." + term, BigDecimal.class, null);
        if (rate == null) {
            switch (term) {
                case 30:
                    rate = new BigDecimal("30");
                    break;
                case 90:
                    rate = new BigDecimal("50");
                    break;
                default:
                    rate = new BigDecimal("20");
            }
        }
        return rate.intValue();
    }

    /**
     * @param term  贷款期限周期规格 单位：天
     * @param count 批准数量比例
     */
    default void updateProjectLoanCountRate(int term, int count) {
        updateSystemString("dating.project.loan.countRate." + term, new BigDecimal(count));
    }

    /**
     * @return 是否启用pay123
     */
    default boolean isEnablePay123() {
        return getSystemString("dating.pay123.enable", BigDecimal.class, BigDecimal.ZERO).intValue() > 0;
    }

    /**
     * 更新pay123状态
     *
     * @param enabled 新状态
     */
    default void updateEnablePay123(boolean enabled) {
        updateSystemString("dating.pay123.enable", enabled ? BigDecimal.ONE : BigDecimal.ZERO);
    }

    /**
     * @return 是否使用畅捷支付套现
     */
    default boolean isEnableChanpay() {
        return getSystemString("dating.chanpay.enable", BigDecimal.class, BigDecimal.ZERO).intValue() > 0;
    }

    /**
     * 更新是否使用畅捷支付套现
     *
     * @param enabled 新状态
     */
    default void updateEnableChanpay(boolean enabled) {
        updateSystemString("dating.chanpay.enable", enabled ? BigDecimal.ONE : BigDecimal.ZERO);
        SystemServiceImpl.UserChanPayForWeixinAB = enabled;
    }

//    double
}
