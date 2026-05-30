package com.claim.claim_processing.rule.ruleGateWay.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.rule.RentalDeductionMapping;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalDeductionMappingRepository
                extends JpaRepository<RentalDeductionMapping, Long> {

        boolean existsByRuleType_IdAndRentalType_IdAndEffectiveFrom(
                        Long ruleTypeId,
                        Long rentalTypeId,
                        LocalDate effectiveFrom);

        boolean existsByRuleType_IdAndRentalType_IdAndEffectiveFromAndIdNot(
                        Long ruleTypeId,
                        Long rentalTypeId,
                        LocalDate effectiveFrom,
                        Long id);

        List<RentalDeductionMapping> findByRuleType_Id(Long ruleTypeId);

        List<RentalDeductionMapping> findByRentalType_Id(Long rentalTypeId);
}
