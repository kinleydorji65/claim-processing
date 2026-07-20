package com.claim.claim_processing.application.repository.application;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;

@Repository
public interface ClaimApplicationDeductionDetailRepository extends JpaRepository<ClaimApplicationDeductionDetail, Long> {


    Optional<ClaimApplicationDeductionDetail> findByClaimApplication_Id(Long applicationId);
}