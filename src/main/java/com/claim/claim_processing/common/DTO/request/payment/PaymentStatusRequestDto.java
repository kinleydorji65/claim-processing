package com.claim.claim_processing.common.DTO.request.payment;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusRequestDto {

    private String code;
    private String name;
    private ActivityEnum isActive;

    private String createdBy;
    private String updatedBy;
}