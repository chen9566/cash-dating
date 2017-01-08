package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.TicketCode;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface TicketCodeRepository extends JpaRepository<TicketCode, TicketCodePK>, JpaSpecificationExecutor<TicketCode> {

    long countByBatch_GoodsAndUsedFalse(TicketGoods goods);
}
