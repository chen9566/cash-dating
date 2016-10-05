package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface BankRepository extends JpaRepository<Bank, String> {
}
