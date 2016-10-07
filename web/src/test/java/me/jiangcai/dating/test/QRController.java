package me.jiangcai.dating.test;

import com.google.zxing.WriterException;
import me.jiangcai.dating.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 仅在测试这出现
 *
 * @author CJ
 */
@Controller
public class QRController {

    @Autowired
    private QRCodeService qrCodeService;

    @RequestMapping(method = RequestMethod.GET, value = "/qrUrl")
    public BufferedImage qr(@RequestParam("url") String url) throws IOException, WriterException {
        return qrCodeService.generateQRCode(url);
    }

}
