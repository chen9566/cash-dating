package me.jiangcai.dating.repository.sale;

import me.jiangcai.dating.entity.sale.CashGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author CJ
 */
public interface CashGoodsRepository extends JpaRepository<CashGoods, Long> {

    @Query("select x from CashGoods as x where x.enable=true and type(x)<>FakeGoods ")
    List<CashGoods> findByEnableTrue();

}
