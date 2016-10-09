package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.VerificationCodePK;
import me.jiangcai.dating.model.VerificationType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

/**
 * 就是验证码啦
 *
 * @author CJ
 */
@Entity
@IdClass(VerificationCodePK.class)
@Setter
@Getter
public class VerificationCode {

    @Id
    private String mobile;
    @Id
    private VerificationType type;

    /**
     * 最近的验证码
     */
    @Column(length = 10, nullable = false)
    private String code;
    /**
     * 验证码过期时间
     */
    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime codeExpireTime;

}
