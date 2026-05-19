package com.claim.claim_processing.common.DTO.update.beneficiary;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimantTypeUpdateRequestDto {

    private String name;
    private String code;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
    private String updatedBy;
}