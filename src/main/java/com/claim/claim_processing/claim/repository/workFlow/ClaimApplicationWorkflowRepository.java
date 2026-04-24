package com.claim.claim_processing.claim.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.claim.entity.workFlow.ClaimApplicationWorkflow;

@Repository
public interface ClaimApplicationWorkflowRepository extends JpaRepository<ClaimApplicationWorkflow, Long> {
    
}
