package com.claim.claim_processing.common.DTO.request.calculationMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationStageRequestDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Integer displayOrder;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;
}