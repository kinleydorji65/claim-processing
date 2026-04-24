package com.claim.claim_processing.claim.repository.application;

import com.claim.claim_processing.claim.entity.application.ClaimApplicationDeductionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimApplicationDeductionDetailRepository extends JpaRepository<ClaimApplicationDeductionDetail, Long> {
}