package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.FinalPayableReviewStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalPayableReviewStatusMasterRepository
        extends JpaRepository<FinalPayableReviewStatusMaster, Long> {

    Optional<FinalPayableReviewStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<FinalPayableReviewStatusMaster> findByIsActive(ActivityEnum isActive);
}