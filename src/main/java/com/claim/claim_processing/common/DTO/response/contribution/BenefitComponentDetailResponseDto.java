package com.claim.claim_processing.common.DTO.response.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitComponentDetailResponseDto {

    private Long id;
    private BenefitComponentTypeResponseDto benefitComponentType;
    private ComponentResponseDto components;
    private ActivityEnum isActive;
}
