package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * @author CJ
 */
@Ignore
public class QRCodeServiceTest extends ServiceBaseTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Ignore
    @Test
    public void hello() throws IOException {
        // 我已经设置的
//        BufferedImage image = ImageIO.read(new URL("http://123.206.99.141:8083/selfweb/tempfile/415f6e628e136d4d20ca96002c9dd2f4.png"));
        // 未设置的
        BufferedImage image = ImageIO.read(new URL("http://123.206.99.141:8083/selfweb/tempfile/4446043c97e5f9e3b7faad10aa85ec9b.png"));

        String url = qrCodeService.scanImage(image);
        System.out.println(url);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(url);
            get.addHeader("user-agent", "MicroMessenger");

            String code = client.execute(get, new BasicResponseHandler());
            System.out.println(code);
        }
        final String sn = url.substring(url.indexOf("=") + 1);
        System.out.println(sn);

        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            // 0 可用了
            // 1 审核中
            // 9 审核驳回了
            // 3 设备异常
            // 其他需注册

            HttpPost get = new HttpPost("http://www.huizhengqiye.com/selfops/dpCardSearchInfoBySN?info=" + sn);
//            get.addHeader("user-agent","MicroMessenger");

            String code = client.execute(get, new BasicResponseHandler());
            System.out.println(code);
        }
    }

}