package com.claim.claim_processing.application.repository.detail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

public interface LegalRecoveryDetailRepository
        extends JpaRepository<LegalRecoveryDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    Optional<LegalRecoveryDetail> findByClaimApplication_Id(Long claimApplicationId);
    Optional<LegalRecoveryDetail> findByClaimDetail_Id(Long claimDetailId);
    List<LegalRecoveryDetail> findByPayeeType_Id(Long payeeTypeId);
}