package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationWorkflow;

import java.util.List;

@Repository
public interface ClaimApplicationWorkflowRepository extends JpaRepository<ClaimApplicationWorkflow, Long> {
    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtAsc(Long claimApplicationId);
    boolean existsByClaimApplication_Id(Long claimApplicationId);

    List<ClaimApplicationWorkflow> findByClaimApplication_IdOrderByActionAtDescCreatedAtDesc(
            Long claimApplicationId
    );
}
