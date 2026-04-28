package com.claim.claim_processing.common.DTO.response.contribution;

import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitComponentTypeDetailResponseDto {

    private Long id;
    private List<BenefitComponentTypeResponseDto> benefitComponentType;
    private List<ComponentResponseDto> components;
    private ActivityEnum isActive;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
