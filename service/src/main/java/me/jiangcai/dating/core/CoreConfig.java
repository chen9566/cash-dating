package me.jiangcai.dating.core;

import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.dating.service")
//@EnableJpaRepositories("me.jiangcai.dating.repository")
public class CoreConfig extends WeixinWebSpringConfig {
}
