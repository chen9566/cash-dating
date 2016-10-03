package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

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
    public void send(@RequestParam  String mobile,@RequestParam VerificationType type) {
        verificationCodeService.sendCode(mobile, type.work());
    }

}
