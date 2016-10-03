package me.jiangcai.dating;

import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.test.WeixinTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

/**
 * @author CJ
 */
@Import({WeixinTestConfig.class, DSConfig.class})
@ImportResource("classpath:/datasource_local.xml")
public class TestConfig {

    @Autowired
    private Environment environment;

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        return new DebugPublicAccountSupplier(environment.getProperty("account.url", "http://localhost/weixin/"));
    }

}
