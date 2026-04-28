package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import lombok.*;

@Data
@Builder
public class ClaimEligibilityResponse {

    private boolean eligible;
    private String matchedRuleCode;
    private String reason;

    private List<String> failedChecks;
}
