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
import java.util.Objects;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
@IdClass(TicketCodePK.class)
@Table(indexes = {@Index(columnList = "used")})
public class TicketCode implements StockToken, Comparable<TicketCode> {
    public static final int CodeLength = 200;
    @Id
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private TicketBatch batch;
    @Id
    @Column(length = CodeLength)
    private String code;
    /**
     * 是否已被用户占去
     */
    private boolean used;
    /**
     * 所有者自己打的标记
     */
    private boolean userFlag;
    @Column(columnDefinition = "datetime")
    private LocalDateTime usedTime;

    public TicketCodePK getTicketCodePK() {
        return new TicketCodePK(batch, code);
    }

    @Override
    public String productSKUCode() {
        return code;
    }

    @Override
    public int compareTo(TicketCode o) {
        return code.compareTo(o.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketCode)) return false;
        TicketCode that = (TicketCode) o;
        return Objects.equals(batch, that.batch) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, code);
    }
}
