package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.ProfitSplit;
import me.jiangcai.dating.entity.SystemString;
import me.jiangcai.dating.entity.support.RateConfig;
import me.jiangcai.dating.repository.SystemStringRepository;
import me.jiangcai.dating.service.SystemService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 系统会固定设置2个数据,用户账面手续费(R),平台（第三方）手续费(r)
 * 其中的差值P = R -r 即为系统可以获取的总的利润,在整体的利润分配中会有三个参与角色
 * <ul>
 * <li>系统（贪婪分配,所谓贪婪分配就是缺少某一角色时,其相应收益会被贪婪。）</li>
 * <li>合伙人</li>
 * <li>发展人</li>
 * </ul>
 * 为了调控合伙人以及发展人的激情,我们可以设置他们可以获取的利润率,同时为了保障系统存在最低的分配率,所以需要设置他们的利润率上限
 * <ul>
 * <li>合伙人 60%</li>
 * <li>发展人 20%</li>
 * </ul>
 *
 * @author CJ
 */
@Service
public class SystemServiceImpl implements SystemService {

    private static final String ChannelRate = "dating.rate.channel";
    private static final String LowestRate = "dating.rate.lowest";
    private static final String BookRate = "dating.rate.book";
    private static final String AgentRate = "dating.rate.agent";
    private static final String GuideRate = "dating.rate.guide";

    private static final Log log = LogFactory.getLog(SystemServiceImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"
            , Locale.CHINA);

    @Autowired
    private SystemStringRepository systemStringRepository;
    //    @Autowired
//    private PublicAccountSupplier supplier;
//    @Autowired
//    private WeixinService weixinService;
    @Autowired
    private Environment environment;


    @PostConstruct
    @Transactional
    @Override
    public void init() throws UnsupportedEncodingException {
        // 无事可做的

    }


    @Override
    public RateConfig currentRateConfig(ProfitSplit profitSplit) {
        profitSplit.validateProfitSplit(this);
        // 我们系统保存2个 1 是用户手续费 2 是平台手续费
        // 剩下的则属于利润,利润分配参与者有3个 系统（贪婪获取）,合伙人,发展人

        // 账面
        BigDecimal bookRate = systemBookRate(profitSplit);
        BigDecimal channelRate = getSystemString(ChannelRate, BigDecimal.class, new BigDecimal("0.0026"));

        BigDecimal profitRate = bookRate.subtract(channelRate);
        // 剩余利益
        BigDecimal agentRate;
        BigDecimal guideRate;

        if (profitRate.compareTo(BigDecimal.ZERO) > 0) {
            double agentProfitRate = profitSplit.agentProfileRate(this);
            double guideProfitRate = profitSplit.guideProfileRate(this);


            if (Double.isNaN(agentProfitRate)) {
                agentRate = BigDecimal.ZERO;
            } else {
                agentRate = profitRate.multiply(BigDecimal.valueOf(agentProfitRate));
            }


            if (Double.isNaN(guideProfitRate)) {
                guideRate = BigDecimal.ZERO;
            } else {
                guideRate = profitRate.multiply(BigDecimal.valueOf(guideProfitRate));
            }
        } else {
            log.warn("negative profitRate for " + profitSplit);
            agentRate = BigDecimal.ZERO;
            guideRate = BigDecimal.ZERO;
        }

        RateConfig config = new RateConfig();
        config.setBookRate(bookRate);
        config.setAgentRate(agentRate);
        config.setChannelRate(channelRate);
        config.setGuideRate(guideRate);
        return config;
    }

    @Override
    public BigDecimal systemBookRate(ProfitSplit profitSplit) {
        if (profitSplit.useLowestRate()) {
            return getSystemString(LowestRate, BigDecimal.class, new BigDecimal("0"));
        }
        BigDecimal rate = profitSplit.bookProfileRate(this);
        if (rate != null)
            return rate;
        return getSystemString(BookRate, BigDecimal.class, new BigDecimal("0.006"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getSystemString(String key, Class<T> exceptedType, T defaultValue) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null)
            return defaultValue;
        if (ss.getValue() == null)
            return defaultValue;
        if (defaultValue instanceof BigDecimal) {
            return (T) new BigDecimal(ss.getValue());
        }
        if (exceptedType == LocalDateTime.class)
            return (T) LocalDateTime.from(dateTimeFormatter.parse(ss.getValue()));
        return (T) ss.getValue();
//        throw new IllegalArgumentException("unknown of default value:" + defaultValue);
    }

    @Override
    public void updateRateConfig(RateConfig rateConfig) {
//        updateSystemString(ChannelRate, rateConfig.getChannelRate().toString());
//        updateSystemString(AgentRate, rateConfig.getAgentRate().toString());
//        updateSystemString(GuideRate, rateConfig.getGuideRate().toString());
    }

    @Override
    public void updateSystemString(String key, String value) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null) {
            ss = new SystemString();
            ss.setId(key);
        }
        ss.setValue(value);
        systemStringRepository.save(ss);
    }

    @Override
    public void updateSystemString(String key, LocalDateTime value) {
        if (value == null) {
            updateSystemString(key, (String) null);
        } else {
            updateSystemString(key, dateTimeFormatter.format(value));
        }
    }
}
