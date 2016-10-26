package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.Bank;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface BankRepository extends JpaRepository<Bank, String> {

    Bank findByName(String name);

    List<Bank> findByDisabledFalse(Sort sort);
}
