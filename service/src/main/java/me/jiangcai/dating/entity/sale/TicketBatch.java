package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 卡券批次
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class TicketBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private TicketGoods goods;
    /**
     * 过期日期
     */
    @Column(columnDefinition = "date")
    private LocalDate expiredDate;
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;
    @ManyToOne
    private User creator;
}
