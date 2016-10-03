package me.jiangcai.dating;

import me.jiangcai.wx.PublicAccountSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author CJ
 */
public class TestConfig {

    @Autowired
    private Environment environment;

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        return new DebugPublicAccountSupplier(environment.getProperty("account.url", "http://localhost/weixin/"));
    }

}
