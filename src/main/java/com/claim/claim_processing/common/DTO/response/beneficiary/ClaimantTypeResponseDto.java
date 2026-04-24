package com.claim.claim_processing.common.DTO.response.beneficiary;

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
public class ClaimantTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}