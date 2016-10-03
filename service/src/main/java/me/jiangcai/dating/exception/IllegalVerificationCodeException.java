package me.jiangcai.dating.exception;

import lombok.Getter;
import me.jiangcai.dating.model.VerificationType;

/**
 * 无效的验证码
 *
 * @author CJ
 */
@Getter
public class IllegalVerificationCodeException extends RuntimeException {

    private final VerificationType type;

    public IllegalVerificationCodeException(VerificationType type) {
        this.type = type;
    }
}
