package com.claim.claim_processing.common.DTO.update.unlcamied;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedStageUpdateDto {

    private String name;
    private Integer displayOrder;
    private ActivityEnum isActive; // "Y" / "N"
}