package com.claim.claim_processing.common.DTO.request.partial;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalRuleRequestDto {

    private Long id;

    private String categoryId;

    private Long reasonId;

    private Long accumulationId;

    private BigDecimal maxWithdrawalPercentage;

    private Integer numberOfContributionMonths;

    private Boolean pfAccumulation;

    private Boolean totalAccumulationValue;

    private String createdBy;

    private String updatedBy;
}