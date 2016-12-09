package me.jiangcai.dating;

import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.model.trj.LoanStatus;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.service.impl.AbstractVerificationCodeService;
import me.jiangcai.dating.service.impl.TourongjiaServiceImpl;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.To;
import me.jiangcai.lib.notice.exception.NoticeException;
import me.jiangcai.wx.test.WeixinTestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;

/**
 * @author CJ
 */
@Import({TestConfig.Config.class, WeixinTestConfig.class, DSConfig.class, ChanpayTestSpringConfig.class})
@ImportResource("classpath:/datasource_local.xml")
@ComponentScan("me.jiangcai.dating.mock")
public class TestConfig {

    private static final Log log = LogFactory.getLog(TestConfig.class);
    @Autowired
    private Environment environment;
    @Autowired
    private BankService bankService;

    @Bean
    @Primary
    public TourongjiaService tourongjiaService() {
        return new TourongjiaServiceImpl(environment) {
            @Override
            public LoanStatus checkLoanStatus(String id) throws IOException {
                return LoanStatus.success;
//                return super.checkLoanStatus(id);
            }
        };
    }

    @Bean
    @Primary
    @DependsOn("initService")
    public ChanpayService chanpayService() {
        return new TestChanpayService();
    }

//    @Bean
//    public PublicAccountSupplier publicAccountSupplier() {
//        return new DebugPublicAccountSupplier(environment.getProperty("account.url", "http://localhost/weixin/"));
//    }

    @Bean
    @Primary
    public VerificationCodeService verificationCodeService() {
        // 1234 always work
        return new AbstractVerificationCodeService() {
            @Override
            protected void send(To to, Content content) throws NoticeException {
                System.err.println("Send Code " + content.asText() + " to " + to.mobilePhone());
            }

            @Override
            protected String generateCode(String mobile, VerificationType type) {
                return "1234";
            }
        };
//        return new VerificationCodeService() {
//            private Set<String> mobiles = new HashSet<>();
//
//            @Override
//            public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {
//                if (!(code.equals("1234") && mobiles.contains(mobile)))
//                    throw new IllegalVerificationCodeException(type);
//            }
//
//            @Override
//            public void sendCode(String mobile, VerificationType type) {
//                log.debug("send code to " + mobile);
//                mobiles.add(mobile);
//            }
//        };
    }

    @PropertySource("classpath:/default_wx.properties")
    static class Config {

    }

}
