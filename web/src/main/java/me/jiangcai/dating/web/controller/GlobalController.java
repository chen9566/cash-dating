package me.jiangcai.dating.web.controller;

import com.google.zxing.WriterException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 公开权限的
 *
 * @author CJ
 */
@Controller
public class GlobalController {

    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private WebApplicationContext applicationContext;
    @Autowired
    private Environment environment;
    @Autowired
    private QRCodeService qrCodeService;


    /**
     * 这是公开uri,所有人都可以参与支付,微信用户 或者 其他用户
     *
     * @param id 订单号
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/toPay/{id}")
    public String toPay(@PathVariable("id") String id) {
        return "hahahah";
    }

    /**
     * 这是公开uri,所有人都可以参与支付,微信用户 或者 其他用户
     * 包括上列url的二维码url
     *
     * @param id 订单号
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/toPayQR/{id}")
    public BufferedImage toPayImage(@PathVariable("id") String id, HttpServletRequest request) throws IOException
            , WriterException {
        StringBuilder urlBuilder = contextUrlBuilder(request);

        urlBuilder.append("/toPay/").append(id);
        return qrCodeService.generateQRCode(urlBuilder.toString());
    }

    private StringBuilder contextUrlBuilder(HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(request.getScheme()).append("://");
        urlBuilder.append(request.getLocalName());
        if (request.getServerPort() == 80 && request.getScheme().equalsIgnoreCase("http"))
            ;
        else if (request.getServerPort() == 443 && request.getScheme().equalsIgnoreCase("https"))
            ;
        else
            urlBuilder.append(":").append(request.getServerPort());

        urlBuilder.append(request.getContextPath());
        return urlBuilder;
    }

    /**
     * 应该在页面的最下方载入
     *
     * @return 所有页面都载入的js
     */
    @RequestMapping(value = "/all.js", method = RequestMethod.GET, produces = "application/javascript")
    public ResponseEntity<String> allScript() throws IOException {
        try (InputStream inputStream = applicationContext.getResource("/mock/all_live.js").getInputStream()) {
            String script = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
            script = script.replaceAll("_TestMode_", String.valueOf(environment.acceptsProfiles("test")));
            script = script.replaceAll("_UriPrefix_", applicationContext.getServletContext().getContextPath());

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(script);
        }
    }

    @RequestMapping(value = "/verificationCode", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void send(@RequestParam String mobile, @RequestParam VerificationType type) {
        verificationCodeService.sendCode(mobile, type.work());
    }

}
