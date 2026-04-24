package com.claim.claim_processing.common.DTO.update.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalPayableReviewStatusUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
}