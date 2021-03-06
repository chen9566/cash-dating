package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    List<LoanRequest> findByLoanData_Owner_OpenIdAndCompletedFalseOrderByCreatedTimeDesc(String openId);

    long countByProcessStatus(LoanRequestStatus status);

    LoanRequest findBySupplierRequestId(String requestId);
}
