package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.PlatformOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface PlatformOrderRepository extends JpaRepository<PlatformOrder, String> {
}
