package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 卡券批次
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Table(indexes = {@Index(columnList = "expiredDate")})
public class TicketBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private TicketGoods goods;
    /**
     * 过期日期
     */
    @Column(columnDefinition = "date")
    private LocalDate expiredDate;
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private User creator;
    @Column(length = 50)
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketBatch)) return false;
        TicketBatch batch = (TicketBatch) o;
        return Objects.equals(goods, batch.goods) &&
                Objects.equals(expiredDate, batch.expiredDate) &&
                Objects.equals(createdTime, batch.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goods, expiredDate, createdTime);
    }
}
