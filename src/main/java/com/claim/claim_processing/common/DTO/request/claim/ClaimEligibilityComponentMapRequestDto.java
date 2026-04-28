package com.claim.claim_processing.common.DTO.request.claim;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEligibilityComponentMapRequestDto {

    private Long id;

    private Long ruleId;

    private Long benefitComponentTypeId;

    @Builder.Default
    private String isActive = "Y";
}