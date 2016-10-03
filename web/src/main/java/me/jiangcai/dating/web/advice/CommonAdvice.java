package me.jiangcai.dating.web.advice;

import me.jiangcai.dating.exception.IllegalVerificationCodeException;
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

}
