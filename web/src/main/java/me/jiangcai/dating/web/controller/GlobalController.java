package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 公开权限的
 *
 * @author CJ
 */
@Controller
public class GlobalController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @RequestMapping(value = "/verificationCode", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void send(String mobile, VerificationType type) {
        verificationCodeService.sendCode(mobile, type.work());
    }

}
