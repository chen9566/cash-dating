package me.jiangcai.dating.web.controller;

import com.google.zxing.WriterException;
import me.jiangcai.chanpay.model.SubBranch;
import me.jiangcai.dating.CashFilter;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.wx.web.mvc.WeixinInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

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
    @Autowired
    private OrderService orderService;
    @Autowired
    private PayResourceService payResourceService;

    public static StringBuilder contextUrlBuilder(HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(request.getScheme()).append("://");
        urlBuilder.append(request.getLocalName());
        if (request.getServerPort() < 0)
            ;
        else if (request.getServerPort() == 80 && request.getScheme().equalsIgnoreCase("http"))
            ;
        else if (request.getServerPort() == 443 && request.getScheme().equalsIgnoreCase("https"))
            ;
        else
            urlBuilder.append(":").append(request.getServerPort());

        urlBuilder.append(request.getContextPath());
        return urlBuilder;
    }

//    /**
//     * 这是公开uri,所有人都可以参与支付,微信用户 或者 其他用户
//     * 这里通常是选择支付方式,选以后 才进入支付环境;在目前的案例中 只有一个选项!所以直接进入建立订单环境
//     *
//     * @param id 订单号
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.GET, value = "/toPay/{id}")
//    public String toPay(@PathVariable("id") String id, Model model) throws IOException, SignatureException {
//        // 如果已完成
//        if (orderService.isComplete(id)) {
//            return "completed.html";
//        }
//        // 如果已建立 平台订单 则直接走平台订单
//        PlatformOrder order = orderService.preparePay(id, PayChannel.weixin);
//        model.addAttribute("order", order);
//        return "pay.html";
//    }

//    /**
//     * 这是公开uri,所有人都可以参与支付,微信用户 或者 其他用户
//     * 包括上列url的二维码url
//     *
//     * @param id 订单号
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.GET, value = "/toPayQR/{id}")
//    public BufferedImage toPayImage(@PathVariable("id") String id, HttpServletRequest request) throws IOException
//            , WriterException {
//        StringBuilder urlBuilder = contextUrlBuilder(request);
//
//        urlBuilder.append("/toPay/").append(id);
//        return qrCodeService.generateQRCode(urlBuilder.toString());
//    }

    public static StringBuilder generateInviteURL(long userId, HttpServletRequest request) {
        StringBuilder urlBuilder = contextUrlBuilder(request);
        urlBuilder.append("/my?");
        urlBuilder.append(CashFilter.guideUserFromId(userId));
        return urlBuilder;
    }

    /**
     * @return id这个人邀请别人加入的二维码
     */
    @RequestMapping(method = RequestMethod.GET, value = "/inviteQR/{id}")
    public BufferedImage inviteQRCode(@PathVariable("id") long id, HttpServletRequest request) throws IOException, WriterException {
        StringBuilder urlBuilder = generateInviteURL(id, request);

        return qrCodeService.generateQRCode(urlBuilder.toString());
    }

    /**
     * 应该在页面的最下方载入
     *
     * @return 所有页面都载入的js
     */
    @RequestMapping(value = "/all.js", method = RequestMethod.GET, produces = "application/javascript")
    public ResponseEntity<String> allScript(HttpServletRequest request) throws IOException {
        try (InputStream inputStream = applicationContext.getResource("/mock/all_live.js").getInputStream()) {
            String script = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
            //
            script = script.replaceAll("_TestMode_", String.valueOf(environment.acceptsProfiles("test")));
            script = script.replaceAll("_UnitTestMode_", String.valueOf(environment.acceptsProfiles("unit_test")));
            script = script.replaceAll("_UriPrefix_", applicationContext.getServletContext().getContextPath());
            script = script.replaceAll("_WeixinEnabled_", String.valueOf(request.getAttribute(WeixinInterceptor.WEIXIN_ENABLED_REQUEST_KEY)));

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("application/javascript"))
                    .body(script);
        }
    }

    @RequestMapping(value = "/verificationCode", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void send(@RequestParam String mobile, @RequestParam VerificationType type) {
        verificationCodeService.sendCode(mobile, type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/provinceList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object provinceList() {
        return PayResourceService.listProvince();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/subBranchList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<SubBranch> subBranchList(String bankId, String cityId) {
        return payResourceService.listSubBranches(cityId, bankId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/subBranchHtml", produces = MediaType.TEXT_HTML_VALUE)
    public String subBranch(String bankId, String cityId, Model model) {
//        model.addAttribute("bankId", bankId);
//        model.addAttribute("cityId", cityId);
        model.addAttribute("list", subBranchList(bankId, cityId));
        return "addcardlist.html";
    }

}
