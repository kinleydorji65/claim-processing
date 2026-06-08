package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationForfeitedComponentPatchRequestDto {
    private Long forfeitedComponentId;
    
    private BigDecimal amount;

    private String reason;

    private String remarks;

    private ActivityEnum isActive;

    private String updatedBy;
}
