package com.claim.claim_processing.integration.contribution.service;

import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;

public interface MemberContributionService {

    MemberContributionSummary getContributionSummary(String nppfNumber, String cid);

}
