package me.jiangcai.dating.entity.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.dating.model.VerificationType;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodePK implements Serializable {

    private static final long serialVersionUID = -2585941455418296648L;

    @Column(length = 15)
    private String mobile;
    private VerificationType type;
}
