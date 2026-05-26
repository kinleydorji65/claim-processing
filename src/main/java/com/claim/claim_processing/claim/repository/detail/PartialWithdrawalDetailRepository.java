package com.claim.claim_processing.claim.repository.detail;

import com.claim.claim_processing.claim.entity.detail.PartialWithdrawalDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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