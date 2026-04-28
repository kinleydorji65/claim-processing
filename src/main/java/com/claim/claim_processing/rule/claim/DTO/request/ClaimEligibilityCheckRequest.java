package com.claim.claim_processing.rule.claim.DTO.request;

import java.util.List;

import lombok.Data;

@Data
public class ClaimEligibilityCheckRequest {
    private Long memberCode;
    private Long cessationTypeId;
    private Long categoryId;
    private List<Long> requestedBenefitComponentTypeIds;
}
