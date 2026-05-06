package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import lombok.*;

@Data
@Builder
public class LapsedRefundPreviewResponseDTO {
    private String matchingRuleCode;
    private String matchingRuleName;
    private List<EligibleBenefitComponentDTO> lapsedBenefits;
}
