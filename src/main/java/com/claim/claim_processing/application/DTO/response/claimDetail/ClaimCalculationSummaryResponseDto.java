package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;
import com.claim.claim_processing.common.DTO.response.others.StatusMasterResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCalculationSummaryResponseDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate calculationEffectiveDate;

    private BigDecimal finalPayableAmount;
    private BigDecimal actualAmountCalculated;
    private BigDecimal totalAmount;

    private String isPfEligible;
    private String isPensionEligible;

    private Integer totalContributionMonth;

    private String recommendedBenefitType;

    private List<ClaimRuleEvaluationListDto> ruleEvaluations;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
