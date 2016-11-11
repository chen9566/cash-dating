package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    List<LoanRequest> findByLoanData_Owner_OpenIdAndCompletedFalseOrderByCreatedTimeDesc(String openId);
}
