package com.claim.claim_processing.common.DTO.response.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionTypeResponseDto {

    private Long id;

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    private String code;
    private String name;
    private String description;

    // -------------------------------
    // STATUS
    // -------------------------------
    private ActivityEnum isActive;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private Timestamp createdAt;
    private String createdBy;

    private Timestamp updatedAt;
    private String updatedBy;
}
