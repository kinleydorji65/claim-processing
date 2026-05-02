package com.claim.claim_processing.common.DTO.request.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalReasonCauseMapRequestDto {

    private Long reasonId;
    private Long causeId;

    private ActivityEnum isActive;
    private String createdBy;
    private String updatedBy;
}