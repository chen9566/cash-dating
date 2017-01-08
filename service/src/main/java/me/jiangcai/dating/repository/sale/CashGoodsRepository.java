package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.CashGoods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface CashGoodsRepository extends JpaRepository<CashGoods, Long> {

    List<CashGoods> findByEnableTrue();

}
