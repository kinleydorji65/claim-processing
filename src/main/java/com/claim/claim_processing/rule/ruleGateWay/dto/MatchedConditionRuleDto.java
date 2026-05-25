package com.claim.claim_processing.rule.ruleGateWay.dto;

import java.time.LocalDate;
import java.util.List;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedConditionRuleDto {

    private Long subRuleId;
    private String ruleCode;
    private String ruleName;

    private Long loanTypeId;
    private String loanType;

    private Long reasonId;
    private String reasonName;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private MatchedConditionResponse condition;

    private List<Components> components;
    private List<RefundTypeDTO> refundTypes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedConditionResponse {
        private Long id;
        private String schemeTypeName;
        private Long schemeTypeId;
        private Long priorityOrder;
        private Long totalContributionNumber;
        private Double withdrawalPercentage;
        private Long minMonths;
        private Long maxMonths;
        private String comparisonType;
        private String isActive;
        private PartialWithdrawalAccumulationResponseDto accumulation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Components {
        private Long componentId;
        private String componentName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundTypeDTO {
        private Long id;
        private String name;
    }
}
