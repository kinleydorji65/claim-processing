package com.claim.claim_processing.common.DTO.response.common;

import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTypeDeductionMapResponseDto {

    private Long id;

    // -------------------------------
    // CLAIM TYPE
    // -------------------------------
    private ClaimTypeMasterResponseDto claimType;

    // -------------------------------
    // DEDUCTION TYPE
    // -------------------------------
    private DeductionTypeResponseDto deductionType;

    // -------------------------------
    // BUSINESS FIELDS
    // -------------------------------
    private Character isAllowed;
    private Integer displayOrder;
    private String remarks;

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
