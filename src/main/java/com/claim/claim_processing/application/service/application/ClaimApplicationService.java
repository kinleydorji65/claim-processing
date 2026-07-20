package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClaimApplicationService {

    ClaimApplication create(ClaimApplicationRequestDto request);

    ClaimApplication update(ClaimApplicationRequestDto request);
    
    ClaimApplication unClaimedBy(String applicationId, String unclaimedBy);

    ClaimApplication claimedBy(String applicationId, String claimedBy);

    ClaimApplication getById(Long id);

    ClaimApplication getByApplicationNumber(String applicationNumber);

    List<ClaimApplication> getAll();

    List<ClaimApplication> getByMemberCode(String memberCode);
    List<ClaimApplication> getVerifiedApplication();
    List<ClaimApplication> getByAgencyCodeAndClaimTypeId(String agencyCode, Long claimTypeId);

    List<ClaimApplication> getByNppfNumber(String nppfNumber);
// In ClaimApplicationService.java - Keep both methods
Page<ClaimApplication> findByInitiatedByAndIsSpecialCase(String initiatedBy, ActivityEnum isSpecialCase, Pageable page);
    List<ClaimApplication> getByUserCodeAndStatusId(String userCode, Long statusId);
    List<ClaimApplication> getByUserCodeAndSpecialClaim(String userCode);
    List<ClaimApplication> getLegalRecoveryWithUserCode(String userCode);
    List<ClaimApplication> getAllSpecialCase();
    List<ClaimApplication> getAllSpecialCaseWithClaimedBy(String claimedBy);
}