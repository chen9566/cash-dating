package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@IdClass(TicketCodePK.class)
public class TicketCode {
    public static final int CodeLength = 50;

    @Id
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private TicketBatch batch;
    @Id
    @Column(length = CodeLength)
    private String code;
    private boolean used;
    @Column(columnDefinition = "datetime")
    private LocalDateTime usedTime;
}
