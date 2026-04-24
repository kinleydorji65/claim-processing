package com.claim.claim_processing.common.DTO.response.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityComponentMapResponseDto {

    private Long id;

    // -------------------------------
    // RULE
    // -------------------------------
    private Long ruleId;
    private String ruleCode;   // optional (if available)
    private String ruleName;   // optional

    // -------------------------------
    // BENEFIT COMPONENT TYPE
    // -------------------------------
    private Long benefitComponentTypeId;
    private String benefitComponentTypeCode; // optional
    private String benefitComponentTypeName; // optional

    // -------------------------------
    // STATUS
    // -------------------------------
    private String isActive;
}
