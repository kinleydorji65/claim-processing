package com.claim.claim_processing.common.DTO.update.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityCategoryMapUpdateRequestDto {

    private Long ruleId;

    private String categoryId;
}