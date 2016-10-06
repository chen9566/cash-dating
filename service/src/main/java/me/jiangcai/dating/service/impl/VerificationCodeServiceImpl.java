package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.service.VerificationCodeService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * @author CJ
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Override
    public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {

    }

    @Override
    public void sendCode(String mobile, Function<String, String> fill) {

    }
}
