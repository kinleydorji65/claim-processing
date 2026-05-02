package com.claim.claim_processing.common.DTO.request.specialCase;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialCaseRefundReasonRequestDto {

    private String code;
    private String name;
    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}