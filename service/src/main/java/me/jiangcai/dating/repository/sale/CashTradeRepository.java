package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.CashTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface CashTradeRepository extends JpaRepository<CashTrade, Long>, JpaSpecificationExecutor<CashTrade> {

    List<CashTrade> findByUser_OpenId(String openId);

}
