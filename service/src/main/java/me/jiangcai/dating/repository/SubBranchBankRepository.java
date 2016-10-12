package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.SubBranchBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface SubBranchBankRepository extends JpaRepository<SubBranchBank, String> {

    List<SubBranchBank> findByCityCodeAndBank_Code(String cityCode, String bankCode);

    long countByBank_Code(String bankCode);

}
