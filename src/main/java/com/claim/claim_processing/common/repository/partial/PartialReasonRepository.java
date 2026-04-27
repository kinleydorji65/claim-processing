package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalReasonMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialReasonRepository extends JpaRepository<PartialWithdrawalReasonMaster, Long> {

    boolean existsByCode(String code);

    Optional<PartialWithdrawalReasonMaster> findByCode(String code);

    List<PartialWithdrawalReasonMaster> findByIsActiveOrderByNameAsc(ActivityEnum isActive);
}