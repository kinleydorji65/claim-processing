package com.claim.claim_processing.application.service.application;

import com.claim.claim_processing.application.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

import java.util.List;

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
    List<ClaimApplication> getByUserCodeAndStatusId(String userCode, Long statusId);
    List<ClaimApplication> getByUserCodeAndSpecialClaim(String userCode);
    List<ClaimApplication> getLegalRecoveryWithUserCode(String userCode);
    List<ClaimApplication> getAllSpecialCase();
    List<ClaimApplication> getAllSpecialCaseWithClaimedBy(String claimedBy);
}