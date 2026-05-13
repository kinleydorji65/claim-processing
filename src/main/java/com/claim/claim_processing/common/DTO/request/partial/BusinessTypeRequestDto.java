package com.claim.claim_processing.common.DTO.request.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTypeRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private String createdBy;
    private String updatedBy;
    private ActivityEnum isActive;
}