package com.claim.claim_processing.common.DTO.request.wrongRemittance;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceErrorTypeRequestDto {

    private String code;
    private String name;
    private String createdBy;
    private String updatedBy;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
}