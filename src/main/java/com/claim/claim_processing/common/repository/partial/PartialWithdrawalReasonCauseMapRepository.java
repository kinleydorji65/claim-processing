package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonCauseMap;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialWithdrawalReasonCauseMapRepository
        extends JpaRepository<PartialWithdrawalReasonCauseMap, Long> {

    Optional<PartialWithdrawalReasonCauseMap> findById(Long id);

    List<PartialWithdrawalReasonCauseMap> findAll();

    // -----------------------
    // FK GETTERS (IMPORTANT)
    // -----------------------

    List<PartialWithdrawalReasonCauseMap> findByReason_Id(Long reasonId);

    List<PartialWithdrawalReasonCauseMap> findByCause_Id(Long causeId);

    List<PartialWithdrawalReasonCauseMap> findByIsActive(ActivityEnum isActive);
}