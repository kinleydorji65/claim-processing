package com.claim.claim_processing.common.DTO.update.refund_master;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcessRefundReasonUpdateDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
}