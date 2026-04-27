package com.claim.claim_processing.common.DTO.response.unclaimed;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedStatusResponseDto {

    private Long id;
    private String code;
    private String name;
    private Integer displayOrder;
    private ActivityEnum isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}