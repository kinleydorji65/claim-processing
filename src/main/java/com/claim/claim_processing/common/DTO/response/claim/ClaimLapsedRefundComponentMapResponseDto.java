package com.claim.claim_processing.common.DTO.response.claim;


import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundComponentMapResponseDto {

    private Long id;

    // -------------------------------
    // RULE (LAPSED REFUND RULE)
    // -------------------------------
    private Long ruleId;
    private String ruleCode;
    private String ruleName;

    // -------------------------------
    // BENEFIT COMPONENT TYPE
    // -------------------------------
    private BenefitComponentTypeResponseDto benefitComponentType;

    // -------------------------------
    // STATUS
    // -------------------------------
    private ActivityEnum isActive;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}