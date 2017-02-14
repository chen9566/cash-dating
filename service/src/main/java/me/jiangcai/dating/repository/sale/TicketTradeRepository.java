package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.TicketTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface TicketTradeRepository extends JpaRepository<TicketTrade, Long>, JpaSpecificationExecutor<TicketTrade> {
}
