package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.VerificationCode;
import me.jiangcai.dating.entity.support.VerificationCodePK;
import me.jiangcai.dating.exception.IllegalVerificationCodeException;
import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.VerificationCodeRepository;
import me.jiangcai.dating.service.VerificationCodeService;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.To;
import me.jiangcai.lib.notice.exception.NoticeException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
public abstract class AbstractVerificationCodeService implements VerificationCodeService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public void verify(String mobile, String code, VerificationType type) throws IllegalVerificationCodeException {
        VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
        if (verificationCode == null)
            throw new IllegalVerificationCodeException(type);
        if (!verificationCode.getCode().equals(code))
            throw new IllegalVerificationCodeException(type);
        // 过期了
        // c > d
        if (LocalDateTime.now().isAfter(verificationCode.getCodeExpireTime()))
            throw new IllegalVerificationCodeException(type);
    }

    @Override
    public void sendCode(String mobile, VerificationType type) {
        // 短时间内不允许 1 分钟?
        // 有效时间 10分钟?
        VerificationCode verificationCode = verificationCodeRepository.findOne(new VerificationCodePK(mobile, type));
        if (verificationCode != null) {
            // c < d - 10 + 1
            if (LocalDateTime.now().isBefore(verificationCode.getCodeExpireTime().minusMinutes(9)))
                throw new IllegalStateException("短时间内不可以重复发送。");
        } else {
            verificationCode = new VerificationCode();
            verificationCode.setMobile(mobile);
            verificationCode.setType(type);
        }

        String code = generateCode(mobile, type);

        // 执行发送
        send(new To() {
            @Override
            public String mobilePhone() {
                return mobile;
            }
        }, new Content() {
            @Override
            public String asText() {
                return type.work().apply(code);
            }
        });

        // 保存数据库
        verificationCode.setCode(code);
        verificationCode.setCodeExpireTime(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(verificationCode);
    }

    /**
     * 实际的发送验证码
     *
     * @param to
     * @param content
     */
    protected abstract void send(To to, Content content) throws NoticeException;

    /**
     * @param mobile
     * @param type
     * @return 生成验证码
     */
    protected abstract String generateCode(String mobile, VerificationType type);
}
