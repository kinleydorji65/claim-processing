package com.claim.claim_processing.common.DTO.request.legalMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryReasonRequestDto {

    private String code;
    private String name;
    private ActivityEnum isActive;
    private String description;
    private Integer displayOrder;
    private String createdBy;
}