package com.claim.claim_processing.common.DTO.response.claim;


import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundCategoryMapResponseDto {

    private Long id;

    // -------------------------------
    // RULE (LAPSED REFUND RULE)
    // -------------------------------
    private ClaimLapsedRefundResponseDto claimLapsedRefundResponse;

    // -------------------------------
    // CATEGORY
    // -------------------------------
    private AgencyCategoryDTO category;
}
