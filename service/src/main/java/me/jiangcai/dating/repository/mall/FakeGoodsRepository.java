package me.jiangcai.dating.repository.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface FakeGoodsRepository extends JpaRepository<FakeGoods, Long>, JpaSpecificationExecutor<FakeGoods> {

    List<FakeGoods> findByDiscountIsNotNullAndEnableTrueAndSpecialTrue();

    List<FakeGoods> findBySpecialFalseAndEnableTrue(Sort sort);
}
