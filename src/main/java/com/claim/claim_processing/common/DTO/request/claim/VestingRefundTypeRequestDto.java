package com.claim.claim_processing.common.DTO.request.claim;

import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VestingRefundTypeRequestDto {

    private String code;
    private String name;
    private String createdBy;
    private ActivityEnum isActive;

    private List<Long> benefitComponentIds; // List of ComponentMaster IDs to be associated with this VestingRefundType
}