package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTypeMasterResponseDto {

    private Long id;

    private String code;

    private String name;

    private String categoryCode;

    private ActivityEnum isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}