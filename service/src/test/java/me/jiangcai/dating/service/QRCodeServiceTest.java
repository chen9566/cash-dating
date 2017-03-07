package me.jiangcai.dating.service;

import com.google.zxing.WriterException;
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
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author CJ
 */
@Ignore
public class QRCodeServiceTest extends ServiceBaseTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Test
    public void qrs() throws IOException, WriterException {
        String[] codes = new String[]{
                "7310183770009244671=8C6C3F5806AA6C86",
                "7310183820009243990=771009CB4E52F330",
                "7310183830009250664=2489817CAF8891BE",
                "7310183850009034989=847245D848FE5850",
                "7310183860009054278=F26EAAA5B30F0A31",
                "7310183860009250655=5620FD1C1D976A03",
                "7310183880009243703=082B72125F8CDCDC",
                "7310183890009252501=DDAEAAE7A0354694",
                "7310183920009243986=3D362850F5ACF68C"
        };

        for (int i = 0; i < codes.length; i++) {
            BufferedImage image = qrCodeService.generateQRCode(codes[i]);
            ImageIO.write(image, "png", new File("./target/start_" + i + ".png"));
        }
    }

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