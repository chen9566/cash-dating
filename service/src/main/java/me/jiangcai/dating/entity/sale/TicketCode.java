package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.goods.stock.StockToken;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@IdClass(TicketCodePK.class)
@Table(indexes = {@Index(columnList = "used")})
public class TicketCode implements StockToken {
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

    @Override
    public String productSKUCode() {
        return code;
    }
}
