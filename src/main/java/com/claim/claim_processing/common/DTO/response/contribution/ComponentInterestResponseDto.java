package com.claim.claim_processing.common.DTO.response.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentInterestResponseDto {

    private Long id;

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    private String code;
    private String name;

    // -------------------------------
    // BASE COMPONENT (FK)
    // -------------------------------
    private List<ComponentResponseDto> components;

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