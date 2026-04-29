package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLapsedRefundComponentMapRequestDto {

    private Long id; // optional for update

    private Long ruleId;

    private Long benefitComponentTypeId;
    private Long claimLapsedRefundCategoryMapId;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;
}