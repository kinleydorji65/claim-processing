package com.claim.claim_processing.common.repository.refund_master;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.refundMaster.RefundScopeMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundScopeRepository extends JpaRepository<RefundScopeMaster, Long> {

    boolean existsByCode(String code);

    Optional<RefundScopeMaster> findByCode(String code);

    List<RefundScopeMaster> findByIsActive(ActivityEnum isActive);

    Optional<RefundScopeMaster> findByCodeAndIsActive(String code, ActivityEnum isActive);
}