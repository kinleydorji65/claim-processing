package com.claim.claim_processing.common.DTO.response.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentResponseDto {

    private Long id;

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    private String code;
    private String name;
    private String componentType;

    // -------------------------------
    // STATUS
    // -------------------------------
    private ActivityEnum isActive;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
