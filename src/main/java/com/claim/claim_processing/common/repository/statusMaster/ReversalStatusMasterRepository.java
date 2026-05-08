package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.ReversalStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReversalStatusMasterRepository
        extends JpaRepository<ReversalStatusMaster, Long> {

    Optional<ReversalStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<ReversalStatusMaster> findByIsActive(ActivityEnum isActive);
}