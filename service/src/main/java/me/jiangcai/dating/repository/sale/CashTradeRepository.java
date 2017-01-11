package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.CashTrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface CashTradeRepository extends JpaRepository<CashTrade, Long> {

    List<CashTrade> findByUser_OpenId(String openId);

}
