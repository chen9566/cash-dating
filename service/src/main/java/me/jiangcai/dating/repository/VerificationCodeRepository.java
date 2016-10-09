package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.VerificationCode;
import me.jiangcai.dating.entity.support.VerificationCodePK;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, VerificationCodePK> {
}
