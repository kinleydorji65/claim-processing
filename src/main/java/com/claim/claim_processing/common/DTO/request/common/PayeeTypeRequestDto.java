package com.claim.claim_processing.common.DTO.request.common;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayeeTypeRequestDto {

    private String code;
    private String name;
    private String description;
    private ActivityEnum isActive;
    private String createdBy;
    private String updatedBy;
}