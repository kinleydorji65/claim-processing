package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VestingResultDto {
    private boolean lumpSumEligible;
    private String refundTypeName;
}
