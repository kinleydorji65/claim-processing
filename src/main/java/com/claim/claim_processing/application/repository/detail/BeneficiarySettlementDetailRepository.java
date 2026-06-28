package com.claim.claim_processing.application.repository.detail;

import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiarySettlementDetailRepository
        extends JpaRepository<BeneficiarySettlementDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    Optional<BeneficiarySettlementDetail> findByClaimApplication_Id(Long claimApplicationId);

    void deleteByClaimApplication_Id(Long claimApplicationId);
    Optional<BeneficiarySettlementDetail> findByClaimDetail_Id(Long claimDetailId);
}