package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;

import java.util.List;

@Repository
public interface ClaimApplicationWorkflowRepository extends JpaRepository<ClaimApplicationWorkflow, Long> {
    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtAsc(Long claimApplicationId);

    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByWorkflowLevelAsc(Long claimApplicationId);

    List<ClaimApplicationWorkflow> findByReferenceNumberOrderByActionAtAsc(String referenceNumber);

    List<ClaimApplicationWorkflow> findByActionByOrderByActionAtDesc(String actionBy);

    boolean existsByClaimApplication_Id(Long claimApplicationId);
}
