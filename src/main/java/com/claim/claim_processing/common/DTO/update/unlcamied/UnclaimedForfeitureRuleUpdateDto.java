package com.claim.claim_processing.common.DTO.update.unlcamied;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedForfeitureRuleUpdateDto {

    private String name;
    private ActivityEnum isActive;
}