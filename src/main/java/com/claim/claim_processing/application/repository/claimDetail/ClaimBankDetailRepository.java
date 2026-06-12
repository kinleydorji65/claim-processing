package com.claim.claim_processing.application.repository.claimDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;

@Repository
public interface ClaimBankDetailRepository extends JpaRepository<ClaimBankDetail, Long> {
}
