package com.claim.claim_processing.claim.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.claim.entity.application.ClaimApplicationBankDetail;

@Repository
public interface ClaimApplicationBankDetailRepository extends JpaRepository<ClaimApplicationBankDetail, Long> {
    
}
