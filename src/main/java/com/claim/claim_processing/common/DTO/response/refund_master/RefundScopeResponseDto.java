package com.claim.claim_processing.common.DTO.response.refund_master;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundScopeResponseDto {

    private Long id;
    private String code;
    private String name;
    private ActivityEnum isActive;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}