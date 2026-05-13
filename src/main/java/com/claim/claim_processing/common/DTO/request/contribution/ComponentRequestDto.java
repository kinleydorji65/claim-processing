package com.claim.claim_processing.common.DTO.request.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentRequestDto {

    private String code;
    private String name;
    private String componentType;
    private String createdBy;
    private String updatedBy;
    private ActivityEnum isActive;
}