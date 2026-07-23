package com.claim.claim_processing.application.repository.claimDetail;

import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCaseComponentDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimSpecialCaseComponentDetailRepository extends JpaRepository<ClaimSpecialCaseComponentDetail, Long> {
    List<ClaimSpecialCaseComponentDetail>   findBySpecialCase_Id(Long specialCaseId);
}