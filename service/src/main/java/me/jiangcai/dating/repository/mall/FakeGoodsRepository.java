package me.jiangcai.dating.repository.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface FakeGoodsRepository extends JpaRepository<FakeGoods, Long> {

    List<FakeGoods> findByDiscountIsNotNullAndEnableTrueAndSpecialTrue();
}
