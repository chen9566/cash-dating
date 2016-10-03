package me.jiangcai.dating.service;

import me.jiangcai.dating.model.VerificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

/**
 * 验证码服务
 *
 * @author CJ
 */
public interface VerificationCodeService {

    /**
     * 验证
     *
     * @param mobile 手机号码
     * @param code
     * @param type   验证码类型
     * @return true for yes
     */
    boolean verify(String mobile, String code, VerificationType type);

    /**
     * 发送验证码
     *
     * @param mobile 手机号码
     * @param fill   这是一个函数,会传入验证码,然后返回最终发给用户的文本
     */
    @Transactional
    void sendCode(String mobile, Function<String, String> fill);

}
