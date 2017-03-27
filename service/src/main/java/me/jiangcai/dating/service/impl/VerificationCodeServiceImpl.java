package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.model.VerificationType;
import me.jiangcai.dating.repository.VerificationCodeRepository;
import me.jiangcai.lib.notice.Content;
import me.jiangcai.lib.notice.NoticeService;
import me.jiangcai.lib.notice.To;
import me.jiangcai.lib.notice.exception.NoticeException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class VerificationCodeServiceImpl extends AbstractVerificationCodeService {

    private final NoticeService noticeService;

    @Autowired
    public VerificationCodeServiceImpl(VerificationCodeRepository verificationCodeRepository, Environment environment
            , NoticeService noticeService) {
        super(verificationCodeRepository, environment);
        this.noticeService = noticeService;
    }

    @Override
    protected void send(To to, Content content) throws NoticeException {
        noticeService.send("zjy://", to, content);
    }

    @Override
    protected String generateCode(String mobile, VerificationType type) {
        return RandomStringUtils.randomNumeric(4);
    }
}
