package com.claim.claim_processing.integration.contribution.service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.contribution.PartialMemberContributionSummary;

public interface MemberContributionService {

    ApiResponseDTO<MemberContributionSummary> getContributionSummary(String nppfNumber);
    ApiResponseDTO<PartialMemberContributionSummary> getPartialContributionSummary(String nppfNumber);
}
