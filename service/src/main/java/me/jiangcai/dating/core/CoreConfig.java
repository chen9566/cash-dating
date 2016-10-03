package me.jiangcai.dating.core;

import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 唯一入口
 *
 * @author CJ
 */
@Configuration
@Import({CommonConfig.class, DataSupportConfig.class})
@ComponentScan("me.jiangcai.dating.service")
@EnableJpaRepositories("me.jiangcai.dating.repository")
public class CoreConfig extends WeixinWebSpringConfig {
}
