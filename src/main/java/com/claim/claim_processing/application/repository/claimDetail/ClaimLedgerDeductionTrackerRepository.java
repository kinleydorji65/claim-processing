package com.claim.claim_processing.application.repository.claimDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerDeductionTracker;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import java.util.List;
import java.util.Optional;

public interface ClaimLedgerDeductionTrackerRepository
        extends JpaRepository<ClaimLedgerDeductionTracker, Long> {

    Optional<ClaimLedgerDeductionTracker> findByClaimDetailId(Long claimId);

    Optional<ClaimLedgerDeductionTracker> findByNppfNumber(String nppfNumber);

    List<ClaimLedgerDeductionTracker> findByIsActive(ActivityEnum isActive);

    boolean existsByClaimDetailId(Long claimId);
}
