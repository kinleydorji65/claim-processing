package com.claim.claim_processing.common.repository.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartialWithdrawalAccumulationRepository
        extends JpaRepository<PartialWithdrawalAccumulationMaster, Long> {

    boolean existsByCode(String code);

    List<PartialWithdrawalAccumulationMaster> findByIsActive(String isActive);

    List<PartialWithdrawalAccumulationMaster> findByIsActive(ActivityEnum isActive);

}