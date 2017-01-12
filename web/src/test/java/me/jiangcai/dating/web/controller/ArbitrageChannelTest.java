package me.jiangcai.dating.web.controller;

import me.jiangcai.chrone.model.AccountStatus;
import me.jiangcai.chrone.test.bean.ChroneTestHelper;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.channel.ChroneService;
import me.jiangcai.dating.model.PayMethod;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 渠道测试
 *
 * @author CJ
 */
public class ArbitrageChannelTest extends WebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ChroneTestHelper chroneTestHelper;

    @Test
    public void chrone() throws IOException {

        if (getSystemService().arbitrageChannel(PayMethod.weixin) == applicationContext.getBean(ChroneService.class)) {
            //继续
            String mobile = helloMobile(null, null);
            chroneTestHelper.getAccountStatusMap().put(mobile, AccountStatus.registered);

            driver.get("http://localhost/start");
            assertThat(driver.getTitle())
                    .isEqualTo("Binding");
        }
    }


}
