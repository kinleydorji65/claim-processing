package com.claim.claim_processing.application.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;

import java.util.List;

@Repository
public interface BeneficiaryClaimantDetailRepository extends JpaRepository<BeneficiaryClaimantDetail, Long> {
    List<BeneficiaryClaimantDetail> findByBeneficiarySettlementDetail_Id(Long beneficiarySettlementDetailId);

    List<BeneficiaryClaimantDetail> findByNominee_Id(Long nomineeId);

    List<BeneficiaryClaimantDetail> findByDependent_Id(Long dependentId);

    boolean existsByBeneficiarySettlementDetail_IdAndBeneficiaryIdentifier(
            Long beneficiarySettlementDetailId,
            String beneficiaryIdentifier
    );

    boolean existsByBeneficiarySettlementDetail_IdAndClaimantType_Id(
            Long beneficiarySettlementDetailId,
            Long claimantTypeId
    );

    void deleteByBeneficiarySettlementDetail_Id(Long beneficiarySettlementDetailId);

}