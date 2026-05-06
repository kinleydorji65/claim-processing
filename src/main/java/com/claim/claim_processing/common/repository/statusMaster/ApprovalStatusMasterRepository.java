package com.claim.claim_processing.common.repository.statusMaster;

import com.claim.claim_processing.common.entities.statusMaster.ApprovalStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStatusMasterRepository extends JpaRepository<ApprovalStatusMaster, Long> {

    Optional<ApprovalStatusMaster> findByCode(String code);

    boolean existsByCode(String code);

    List<ApprovalStatusMaster> findByIsActive(ActivityEnum isActive);

}