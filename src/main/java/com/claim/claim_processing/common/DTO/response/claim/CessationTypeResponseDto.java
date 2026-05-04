package com.claim.claim_processing.common.DTO.response.claim;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CessationTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private ClaimCircumstanceResponseDto claimCircumstance;
    private ActivityEnum isActive;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}