package com.claim.claim_processing.common.DTO.request.common;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowReasonRequestDto {

    private String code;
    private String name;
    private String reasonType;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
    private String createdBy;
    private String updatedBy;
}