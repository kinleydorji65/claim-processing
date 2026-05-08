package com.claim.claim_processing.common.DTO.request.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedStatusRequestDto {

    private String code;
    private String name;
    private Integer displayOrder;
    private ActivityEnum isActive;
}