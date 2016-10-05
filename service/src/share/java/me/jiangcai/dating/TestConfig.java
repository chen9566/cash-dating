package me.jiangcai.dating;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.service.impl.AbstractChanpayService;
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
@Import({WeixinTestConfig.class, DSConfig.class, ChanpayTestSpringConfig.class})
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
    public ChanpayService chanpayService() {
        return new AbstractChanpayService() {
            @Override
            protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
                request.setBankCode("WXPAY");
            }
        };
    }

    @Bean
    @Primary
    public VerificationCodeService verificationCodeService() {
        // 1234 always work
        return new VerificationCodeService() {
            private Set<String> mobiles = new HashSet<>();

            @Override
            public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {
                if (!(code.equals("1234") && mobiles.contains(mobile)))
                    throw new IllegalVerificationCodeException(type);
            }

            @Override
            public void sendCode(String mobile, Function<String, String> fill) {
                log.debug("send code to " + mobile);
                mobiles.add(mobile);
            }
        };
    }

}
