package com.claim.claim_processing.common.DTO.response.partial;

import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalRuleResponseDto {

    private Long id;

    private AgencyCategoryDTO category;

    private PartialWithdrawalReasonResponseDto reason;

    private PartialWithdrawalAccumulationResponseDto accumulation;

    private BigDecimal maxWithdrawalPercentage;

    private Integer numberOfContributionMonths;

    private ActivityEnum isActive;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}