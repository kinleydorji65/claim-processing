package com.claim.claim_processing.application.repository.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;

@Repository
public interface ClaimApplicationBankDetailRepository extends JpaRepository<ClaimApplicationBankDetail, Long> {
    List<ClaimApplicationBankDetail> findByClaimApplication_ApplicationNumber(String applicationNumber);
}
