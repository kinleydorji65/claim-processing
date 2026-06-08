package com.claim.claim_processing.application.DTO.response.calculation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.DTO.response.others.StatusMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryResponseDto {

    private Long id;

    private Long claimApplicationId;
    private String applicationNumber;

    private StageResponseDto calculationStage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate calculationEffectiveDate;

    private BigDecimal finalPayableAmount;
    private BigDecimal actualAmountCalculated;

    private String isPfEligible;
    private String isPensionEligible;

    private Integer totalContributionMonth;

    private String recommendedBenefitType;

    private StatusMasterResponseDto calculationStatus;

    private ActivityEnum isActive;

    private List<ClaimApplicationRuleEvaluationListDto> ruleEvaluations;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}