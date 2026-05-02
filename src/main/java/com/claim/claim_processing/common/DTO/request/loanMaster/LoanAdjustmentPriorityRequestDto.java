package com.claim.claim_processing.common.DTO.request.loanMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAdjustmentPriorityRequestDto {
    private Long loanTypeId;
    private Integer priorityOrder;
    private String description;
    private ActivityEnum isActive;
    private String createdBy;
    private String updatedBy;
}