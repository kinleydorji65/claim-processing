package com.claim.claim_processing.common.DTO.response.loan;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAdjustmentPriorityResponseDto {

    private Long id;

    // -------------------------------
    // LOAN TYPE (FK)
    // -------------------------------
    private Long loanTypeId;
    private String loanTypeCode;
    private String loanTypeName;

    // -------------------------------
    // PRIORITY INFO
    // -------------------------------
    private Integer priorityOrder;
    private String description;

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
