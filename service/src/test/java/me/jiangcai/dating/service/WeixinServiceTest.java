package me.jiangcai.dating.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author CJ
 */
public class WeixinServiceTest extends ServiceBaseTest {

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

        String msg = "%5B%7B%22name%22%3A%22%E4%B9%B0%E5%8D%95%22%2C%22type%22%3A%22view%22%2C%22data%22%3A%22http%3A%2F%2Fapp.kuanyes.com%2Fcash%2Fstart%22%7D%2C%7B%22name%22%3A%22%E6%8E%A8%E5%B9%BF%22%2C%22type%22%3A%22view%22%2C%22data%22%3A%22http%3A%2F%2Fapp.kuanyes.com%2Fcash%2FmyInviteCode%22%7D%2C%7B%22name%22%3A%22%E6%88%91%E7%9A%84%22%2C%22type%22%3A%22view%22%2C%22data%22%3A%22http%3A%2F%2Fapp.kuanyes.com%2Fcash%2Fmy%22%7D%5D";

        weixinService.menus(msg, account);

    }

}