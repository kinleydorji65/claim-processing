package com.claim.claim_processing.common.DTO.response.claim;

import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimVestingRuleMasterResponseDto {

    private Long id;

    private String ruleCode;

    private AgencyCategoryDTO category;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String refundType;

    private Integer minVestingMonths;

    private Integer maxVestingMonths;

    private String comparisonType;

    private String payoutResult;

    private String remarks;

    private ActivityEnum isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;

}