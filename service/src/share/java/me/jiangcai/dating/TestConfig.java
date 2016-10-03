package me.jiangcai.dating;

import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.test.WeixinTestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author CJ
 */
@Import({WeixinTestConfig.class, DSConfig.class})
@ImportResource("classpath:/datasource_local.xml")
public class TestConfig {

    private static final Log log = LogFactory.getLog(TestConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        return new DebugPublicAccountSupplier(environment.getProperty("account.url", "http://localhost/weixin/"));
    }

    @Bean
    @Primary
    public VerificationCodeService verificationCodeService() {
        // 1234 always work
        return new VerificationCodeService() {
            private Set<String> mobiles = new HashSet<>();

            @Override
            public boolean verify(String mobile, String code) {
                return code.equals("1234") && mobiles.contains(mobile);
            }

            @Override
            public void sendCode(String mobile, Function<String, String> fill) {
                log.debug("send code to " + mobile);
                mobiles.add(mobile);
            }
        };
    }

}