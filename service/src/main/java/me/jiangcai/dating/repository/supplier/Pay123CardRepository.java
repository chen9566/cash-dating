package me.jiangcai.dating.repository.supplier;

import me.jiangcai.dating.entity.supplier.Pay123Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author CJ
 */
public interface Pay123CardRepository extends JpaRepository<Pay123Card, String> {

    @Query("select card from Pay123Card as card where card not in (select e.pay123Card from UserPaymentExtend as e)")
    List<Pay123Card> findAllUnused();

    @Query("select count(card) from Pay123Card as card where card not in (select e.pay123Card from UserPaymentExtend as e)")
    long countAllUnused();

    Pay123Card findByQrUrl(String url);
}
