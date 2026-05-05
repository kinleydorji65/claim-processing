package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalBenefitMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartialWithdrawalBenefitMapRepository
        extends JpaRepository<PartialWithdrawalBenefitMap, Long> {

    // 🔹 Fetch by accumulation FK
    List<PartialWithdrawalBenefitMap> findByAccumulation_Id(Long accumulationId);

    // 🔹 Fetch by benefit component FK
    List<PartialWithdrawalBenefitMap> findByBenefitComponent_Id(Long benefitComponentId);
}