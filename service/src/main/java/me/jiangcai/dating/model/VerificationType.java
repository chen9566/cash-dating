package me.jiangcai.dating.model;

import java.util.function.Function;

/**
 * @author CJ
 */
public enum VerificationType {
    register,
    card;

    public Function<String, String> work() {
        if (this == register)
            return code -> "您好，您的验证码是" + code + "，请勿泄露给他人。";

        return Function.identity();
    }
}
