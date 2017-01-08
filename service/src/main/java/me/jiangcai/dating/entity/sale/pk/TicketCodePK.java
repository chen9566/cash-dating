package me.jiangcai.dating.entity.sale.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.dating.entity.sale.TicketBatch;

import javax.persistence.Column;
import java.io.Serializable;

import static me.jiangcai.dating.entity.sale.TicketCode.CodeLength;

/**
 * @author CJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCodePK implements Serializable {
    private Long batch;
    @Column(length = CodeLength)
    private String code;

    public TicketCodePK(TicketBatch batch, String code) {
        this(batch.getId(), code);
    }
}
