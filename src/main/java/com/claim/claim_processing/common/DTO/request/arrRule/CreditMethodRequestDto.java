package com.claim.claim_processing.common.DTO.request.arrRule;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditMethodRequestDto {

    // Used for UPDATE (null for CREATE)
    private Long id;

    private String code;
    private String name;
    private String description;

    private Integer displayOrder;

    private ActivityEnum isActive;
}