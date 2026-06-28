package com.claim.claim_processing.application.repository.detail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Repository
public interface PartialWithdrawalDetailRepository
        extends JpaRepository<PartialWithdrawalDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    Optional<PartialWithdrawalDetail> findByClaimApplication_Id(Long claimApplicationId);
    Optional<PartialWithdrawalDetail> findByClaimDetail_Id(Long claimDetailId);

    List<PartialWithdrawalDetail> findByPayeeType_Id(Long payeeTypeId);

    List<PartialWithdrawalDetail> findByWithdrawalReason_Id(Long withdrawalReasonId);

    List<PartialWithdrawalDetail> findByBusinessType_Id(Long businessTypeId);
}