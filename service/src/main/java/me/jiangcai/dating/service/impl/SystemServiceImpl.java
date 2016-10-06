package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.SystemString;
import me.jiangcai.dating.entity.support.RateConfig;
import me.jiangcai.dating.repository.SystemStringRepository;
import me.jiangcai.dating.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * @author CJ
 */
@Service
public class SystemServiceImpl implements SystemService {

    private static final String ChannelRate = "dating.rate.channel";
    private static final String AgentRate = "dating.rate.agent";
    private static final String GuideRate = "dating.rate.guide";

    @Autowired
    private SystemStringRepository systemStringRepository;


    @PostConstruct
    @Transactional
    @Override
    public void init() {
        // 无事可做的
    }

    @Override
    public RateConfig currentRateConfig() {
        BigDecimal channelRate = getSystemString(ChannelRate, BigDecimal.valueOf(0.0025));
        BigDecimal agentRate = getSystemString(AgentRate, BigDecimal.valueOf(0.003));
        BigDecimal guideRate = getSystemString(GuideRate, BigDecimal.valueOf(0.0005));
        RateConfig config = new RateConfig();
        config.setAgentRate(agentRate);
        config.setChannelRate(channelRate);
        config.setGuideRate(guideRate);
        return config;
    }

    @SuppressWarnings("unchecked")
    private <T> T getSystemString(String key, T defaultValue) {
        SystemString ss = systemStringRepository.findOne(key);
        if (ss == null)
            return defaultValue;
        if (defaultValue instanceof BigDecimal) {
            return (T) new BigDecimal(ss.getValue());
        }
        throw new IllegalArgumentException("unknown of default value:" + defaultValue);
    }

    @Override
    public void updateRateConfig(RateConfig rateConfig) {
        updateSystemString(ChannelRate, rateConfig.getChannelRate().toString());
        updateSystemString(AgentRate, rateConfig.getAgentRate().toString());
        updateSystemString(GuideRate, rateConfig.getGuideRate().toString());
    }

    private void updateSystemString(String key, String value) {
        SystemString ss = systemStringRepository.getOne(key);
        if (ss == null) {
            ss = new SystemString();
            ss.setId(key);
        }
        ss.setValue(value);
        systemStringRepository.save(ss);
    }
}
