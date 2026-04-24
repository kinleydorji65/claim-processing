package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminationReasonRepository extends JpaRepository<TerminationReasonMaster, Long> {

    Optional<TerminationReasonMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<TerminationReasonMaster> findByIsActiveOrderByDisplayOrderAsc(ActivityEnum isActive);

    Optional<TerminationReasonMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}