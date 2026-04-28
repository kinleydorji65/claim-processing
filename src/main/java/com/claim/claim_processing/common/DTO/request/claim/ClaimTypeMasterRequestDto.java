package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTypeMasterRequestDto {

    private Long id; // optional for update

    private String code;

    private String name;

    private String categoryCode;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;
}