package com.claim.claim_processing.integration.contribution.service;

import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;

public interface MemberContributionService {

    MemberContributionSummary getContributionSummary(String memberCode);

}
