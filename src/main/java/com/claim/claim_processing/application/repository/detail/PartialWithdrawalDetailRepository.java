package com.claim.claim_processing.application.repository.detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Repository
public interface PartialWithdrawalDetailRepository
        extends JpaRepository<PartialWithdrawalDetail, Long> {

    boolean existsByClaimApplication_Id(Long claimApplicationId);

    boolean existsByClaimApplication_IdAndIdNot(Long claimApplicationId, Long id);

    Optional<PartialWithdrawalDetail> findByClaimApplication_Id(Long claimApplicationId);

    List<PartialWithdrawalDetail> findByPayeeType_Id(Long payeeTypeId);

    List<PartialWithdrawalDetail> findByWithdrawalReason_Id(Long withdrawalReasonId);

    List<PartialWithdrawalDetail> findByWithdrawalCause_Id(Long withdrawalCauseId);

    List<PartialWithdrawalDetail> findByPartialWithdrawalMaster_Id(Long partialWithdrawalMasterId);

    List<PartialWithdrawalDetail> findByDisasterType_Id(Long disasterTypeId);

    List<PartialWithdrawalDetail> findByBusinessType_Id(Long businessTypeId);
}