package com.claim.claim_processing.rule.ruleGateWay.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.rule.LoanDeductionMapping;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanDeductionMappingRepository
                extends JpaRepository<LoanDeductionMapping, Long> {

        boolean existsByRuleType_IdAndLoanType_IdAndEffectiveFrom(
                        Long ruleTypeId,
                        Long loanTypeId,
                        LocalDate effectiveFrom);

        boolean existsByRuleType_IdAndLoanType_IdAndEffectiveFromAndIdNot(
                        Long ruleTypeId,
                        Long loanTypeId,
                        LocalDate effectiveFrom,
                        Long id);

        List<LoanDeductionMapping> findByRuleType_Id(Long ruleTypeId);

        List<LoanDeductionMapping> findByLoanType_Id(Long loanTypeId);
}
