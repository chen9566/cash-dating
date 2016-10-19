package me.jiangcai.dating;

import me.jiangcai.chanpay.Dictionary;
import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.chanpay.test.ChanpayTestSpringConfig;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.entity.ChanpayWithdrawalOrder;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.ChanpayService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.dating.service.impl.AbstractChanpayService;
import me.jiangcai.dating.service.impl.AbstractVerificationCodeService;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.To;
import me.jiangcai.lib.notice.exception.NoticeException;
import me.jiangcai.wx.test.WeixinTestConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author CJ
 */
@Import({TestConfig.Config.class, WeixinTestConfig.class, DSConfig.class, ChanpayTestSpringConfig.class})
@ImportResource("classpath:/datasource_local.xml")
public class TestConfig {

    private static final Log log = LogFactory.getLog(TestConfig.class);
    @Autowired
    private Environment environment;
    @Autowired
    private BankService bankService;

    @Bean
    @Primary
    @DependsOn("initService")
    public ChanpayService chanpayService() {
        return new AbstractChanpayService() {
            @Override
            public String QRCodeImageFromOrder(ChanpayOrder order) throws IllegalStateException, IOException {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("http://localhost/qrUrl?url=")
                        .append(URLEncoder.encode(order.getUrl(), "UTF-8"));
                return stringBuilder.toString();
            }

            @Override
            protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
                request.setBankCode("WXPAY");
            }

            @Override
            protected void beforeExecute(CashOrder order, ChanpayWithdrawalOrder withdrawalOrder, Card card) {
                // 为了确保提现成功 我们使用测试的数据
                Address address = new Address();
                address.setProvince(Dictionary.findByName(Province.class, "上海市"));
                address.setCity(address.getProvince().getCityList().stream()
                        .filter(city -> city.getName().equals("上海市"))
                        .findAny()
                        .orElse(null));

                withdrawalOrder.setAddress(address);

                withdrawalOrder.setBank(bankService.byName("招商银行"));
                withdrawalOrder.setSubBranch("中国招商银行上海市浦建路支行");
                withdrawalOrder.setOwner("测试01");
                withdrawalOrder.setNumber("6214830215878947");
            }
        };
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
