package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.supplier.Pay123Card;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * 开放向第三方支付的扩展信息
 *
 * @author CJ
 */
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "PAY123CARD_ID")
})
@Setter
@Getter
public class UserPaymentExtend {
    @Id
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PAY123CARD_ID")
    private Pay123Card pay123Card;
    /**
     * 分配pay123的时间;我们会定期向pay123相关权限组发送 有人新分配了台卡的推送信息
     */
    @Column(columnDefinition = "datetime")
    private LocalDateTime pay123AssignTime;
}
