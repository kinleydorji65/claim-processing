package com.claim.claim_processing.common.DTO.request.claim;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveAccountRequestDto {

    private Long id;

    private String reserveAccountCode;

    private String reserveAccountName;

    private Long accountTypeId;

    private Long schemeTypeId;

    private ActivityEnum isActive;

    private String createdBy;

    private String updatedBy;
}