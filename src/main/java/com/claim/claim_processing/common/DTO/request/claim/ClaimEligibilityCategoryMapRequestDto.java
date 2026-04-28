package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityCategoryMapRequestDto {

    private Long ruleId;

    private String categoryId;
}