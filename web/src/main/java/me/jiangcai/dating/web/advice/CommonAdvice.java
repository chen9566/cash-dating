package me.jiangcai.dating.web.advice;

import me.jiangcai.dating.entity.LoginToken;
import me.jiangcai.dating.exception.ArbitrageBindRequireException;
import me.jiangcai.dating.exception.ArbitrageBindingException;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.exception.RequestedException;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.wx.web.exception.NoWeixinClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author CJ
 */
@ControllerAdvice
public class CommonAdvice {

    @Autowired
    private UserService userService;

    // ArbitrageBindFailedException

    @ExceptionHandler(ArbitrageBindRequireException.class)
    public String arbitrageBindRequireException() {
        return "redirect:/card?nextAction=/start&workModel=bind";
    }

    @ExceptionHandler(ArbitrageBindingException.class)
    public String arbitrageBindingException() {
        return "binding.html";
    }

    /**
     * 不是微信平台的话,我们会给出一个二维码 让它使用微信用户扫码以完成登录
     *
     * @param ex ex
     * @return
     */
    @ExceptionHandler(NoWeixinClientException.class)
    public String noWeixinClientException(NoWeixinClientException ex, HttpServletRequest request, Model model) {
        LoginToken token = userService.requestLogin(request);
        model.addAttribute("token", token);
        return "manage/login.html";
//        return "kinglist.html";
    }

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
    public void requestedException(RequestedException ex, HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        if (notAjax(ex, request, response))
            throw ex;
    }

    private boolean notAjax(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")))
            return true;
//        if (!"XMLHttpRequest".equalsIgnoreCase(httpHeaders.getFirst("X-Requested-With")))
//            return true;
//        if (!ajaxRequest.equalsIgnoreCase("XMLHttpRequest"))
//            return true;
        //开始ajax处理
        response.setContentType("text/plant; charset=UTF-8");
        response.setStatus(400);
        try (Writer writer = response.getWriter()) {
            writer.write(ex.getMessage());
            writer.flush();
        }
//        response.sendError(400);
        return false;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(IllegalArgumentException ex
            , HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (notAjax(ex, request, response))
            throw ex;
    }
}
