package com.claim.claim_processing.common.repository.specialCase;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialCaseRefundReasonMasterRepository extends JpaRepository<SpecialCaseRefundReasonMaster, Long> {

    Optional<SpecialCaseRefundReasonMaster> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<SpecialCaseRefundReasonMaster> findByIsActive(ActivityEnum isActive);
}