package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VestingRefundTypeResponseDto {

    private Long id;
    private String code;
    private String name;
    private ActivityEnum isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    private List<BenefitComponentTypeResponseDto> benefitComponentTypes;
}