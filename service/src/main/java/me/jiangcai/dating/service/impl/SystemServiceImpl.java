package me.jiangcai.dating.service.impl;

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
 * @author CJ
 */
@Service
public class SystemServiceImpl implements SystemService {

    private static final String ChannelRate = "dating.rate.channel";
    private static final String AgentRate = "dating.rate.agent";
    private static final String GuideRate = "dating.rate.guide";

    private static final Log log = LogFactory.getLog(SystemServiceImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);

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
    public RateConfig currentRateConfig() {
        BigDecimal channelRate = getSystemString(ChannelRate, BigDecimal.class, BigDecimal.valueOf(0.0025));
        BigDecimal agentRate = getSystemString(AgentRate, BigDecimal.class, BigDecimal.valueOf(0.003));
        BigDecimal guideRate = getSystemString(GuideRate, BigDecimal.class, BigDecimal.valueOf(0.0005));
        RateConfig config = new RateConfig();
        config.setAgentRate(agentRate);
        config.setChannelRate(channelRate);
        config.setGuideRate(guideRate);
        return config;
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
        updateSystemString(ChannelRate, rateConfig.getChannelRate().toString());
        updateSystemString(AgentRate, rateConfig.getAgentRate().toString());
        updateSystemString(GuideRate, rateConfig.getGuideRate().toString());
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
