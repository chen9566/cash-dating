package me.jiangcai.dating.core;

import me.jiangcai.chanpay.config.ChanpayConfig;
import me.jiangcai.chrone.config.ChroneConfig;
import me.jiangcai.dating.aop.Join;
import me.jiangcai.goods.core.config.ServiceConfig;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * 唯一入口
 *
 * @author CJ
 */
@Configuration
@Import({CommonConfig.class, DataSupportConfig.class, ChanpayConfig.class, ChroneConfig.class, ServiceConfig.class})
@ComponentScan({"me.jiangcai.dating.service", "me.jiangcai.dating.channel"})
@EnableJpaRepositories("me.jiangcai.dating.repository")
public class CoreConfig extends WeixinWebSpringConfig {

    @Bean
    public Join join() {
        return new Join();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}
