package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityCategoryMapResponseDto {

    private Long id;

    // -------------------------------
    // RULE DETAILS
    // -------------------------------
    private ClaimEligibilityResponseDto rule;

    // -------------------------------
    // CATEGORY DETAILS
    // -------------------------------
    private AgencyCategoryDTO category;

    // -------------------------------
    // STATUS (Future Safe)
    // -------------------------------
    private String isActive;

    // -------------------------------
    // AUDIT (Future Safe)
    // -------------------------------
    private String createdBy;
    private String createdAt;

    private String updatedBy;
    private String updatedAt;
}