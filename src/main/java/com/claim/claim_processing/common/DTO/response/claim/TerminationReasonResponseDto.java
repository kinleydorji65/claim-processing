package com.claim.claim_processing.common.DTO.response.claim;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationReasonResponseDto {

    private Long id;
    private String code;
    private String name;
    private ActivityEnum isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
