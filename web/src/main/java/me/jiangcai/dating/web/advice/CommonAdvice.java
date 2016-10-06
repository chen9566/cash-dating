package me.jiangcai.dating.web.advice;

import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.exception.RequestedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author CJ
 */
@ControllerAdvice
public class CommonAdvice {

    @ExceptionHandler(IllegalVerificationCodeException.class)
    public String illegalVerificationCodeException(IllegalVerificationCodeException ex) {
        switch (ex.getType()) {
            case register:
                return "redirect:/login";
            default:
                throw new IllegalStateException("unknown of " + ex.getType());
        }
    }


//Accept:*/*
//Accept-Encoding:gzip, deflate
//Accept-Language:en-US,en;q=0.8
//Connection:keep-alive
//Content-Length:26
//Content-Type:application/x-www-form-urlencoded; charset=UTF-8
//Cookie:Idea-390470b1=20f86e89-5fd1-46f7-b19e-172f714e3451
//Host:localhost:63342
//Origin:http://localhost:63342
//Referer:http://localhost:63342/cash-dating/web/src/main/webapp/agent.html
//User-Agent:Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1 wechatdevtools/0.7.0 MicroMessenger/6.3.9 Language/zh_CN webview/0
//X-Requested-With:XMLHttpRequest

    @ExceptionHandler(RequestedException.class)
    public void requestedException(RequestedException ex) {

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(IllegalArgumentException ex) {

    }
}
