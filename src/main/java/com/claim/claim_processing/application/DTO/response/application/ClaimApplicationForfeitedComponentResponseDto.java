package com.claim.claim_processing.application.DTO.response.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationForfeitedComponentResponseDto {

    private Long id;

    // Parent
    private Long claimApplicationId;
    private String applicationNumber;

    // Component Details
    private String componentCode;
    private String componentName;
    private String componentType;

    // Amount
    private BigDecimal amount;

    // Rule Information
    private String ruleCode;
    private String subClaimCode;

    // Reason
    private String reason;

    // Status
    private ActivityEnum isActive;

    // Audit
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
