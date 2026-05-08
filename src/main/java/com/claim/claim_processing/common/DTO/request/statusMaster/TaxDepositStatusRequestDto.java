package com.claim.claim_processing.common.DTO.request.statusMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxDepositStatusRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
    private ActivityEnum isActive;
}