package me.jiangcai.dating.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.dating.TestConfig;
import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, CoreConfig.class})
public class WeixinServiceTest extends SpringWebTest {

    @Autowired
    private WeixinService weixinService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private PublicAccountSupplier supplier;

    @Test
    public void menus() throws Exception {
        System.out.println(weixinService);

        PublicAccount account = supplier.findByHost(null);
//        weixinService.
        MenuType a;

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("/menus.json").getInputStream()) {
            String code = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));

            String jsonLine = mapper.writeValueAsString(mapper.readTree(code));

            System.out.println(jsonLine.replaceAll("\"", "\""));
            System.out.println(URLEncoder.encode(jsonLine, "UTF-8"));
//            System.out.println();

            weixinService.menus(code, account);
        }


    }

}