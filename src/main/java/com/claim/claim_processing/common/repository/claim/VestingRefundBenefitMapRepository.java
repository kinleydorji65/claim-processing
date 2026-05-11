package com.claim.claim_processing.common.repository.claim;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.common.entities.claim.VestingRefundBenefitMap;

@Repository
public interface VestingRefundBenefitMapRepository extends JpaRepository<VestingRefundBenefitMap, Long> {
    List<VestingRefundBenefitMap> findByVestingRefundType_Id(Long refundTypeId);
    Optional<VestingRefundBenefitMap> findByVestingRefundType_IdAndBenefitComponentType_Id(Long refundTypeId, Long benefitComponentTypeId);
}
