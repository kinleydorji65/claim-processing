package com.claim.claim_processing.common.repository.refundMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refundMaster.ExcessRefundReasonMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcessRefundReasonRepository extends JpaRepository<ExcessRefundReasonMaster, Long> {

    boolean existsByCode(String code);

    Optional<ExcessRefundReasonMaster> findByCode(String code);

    List<ExcessRefundReasonMaster> findByIsActive(ActivityEnum isActive);

    Optional<ExcessRefundReasonMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);

    List<ExcessRefundReasonMaster> findByIsActiveOrderByDisplayOrderAscNameAsc(ActivityEnum isActive);

    Optional<ExcessRefundReasonMaster> findByIdAndIsActive(Long id, ActivityEnum isActive);
}