package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface CardRepository extends JpaRepository<Card, Long> {
}
