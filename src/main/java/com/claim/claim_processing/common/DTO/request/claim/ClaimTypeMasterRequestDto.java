package com.claim.claim_processing.common.DTO.request.claim;

import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTypeMasterRequestDto {

    private String code;

    private String name;

    private String categoryCode;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;

    private List<Long> ruleTypeIds;
}