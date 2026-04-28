package com.claim.claim_processing.common.DTO.request.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitComponentTypeMasterRequestDto {

    private String code;
    private String name;
    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}