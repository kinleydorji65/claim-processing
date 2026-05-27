package com.claim.claim_processing.application.repository.workFlow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;

@Repository
public interface ClaimApplicationApprovalRepository extends JpaRepository<ClaimApplicationApproval, Long>  {
    
}
