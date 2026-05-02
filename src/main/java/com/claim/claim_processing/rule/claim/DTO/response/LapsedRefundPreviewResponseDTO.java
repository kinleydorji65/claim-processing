package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;

import lombok.*;

@Data
@Builder
public class LapsedRefundPreviewResponseDTO {
    private String matchingRuleCode;
    private String matchingRuleName;
    private MemberContributionSummary contributionSummary;
    private List<EligibleBenefitComponentDTO> lapsedBenefits;
}
