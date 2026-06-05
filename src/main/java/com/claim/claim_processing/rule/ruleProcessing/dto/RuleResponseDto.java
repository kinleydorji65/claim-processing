package com.claim.claim_processing.rule.ruleProcessing.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleResponseDto {
    private Long id;

    private String code;
    private String name;
    private String ruleEffect;
    private Boolean stopOnSuccess;
    private List<ClaimRuleResponseDto> subClaimRules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimRuleResponseDto {

        private Long id;

        private String ruleCode;
        private String ruleName;
        private String loanType;
        private Long ruleTypeId;
    private Long loanTypeId;
    private Long partialReasonId;
        private String partialReason;

        private String description;

        private String stopOnSuccess;

        private LocalDate effectiveFrom;

        private LocalDate effectiveTo;

        private String isActive;

        private String createdBy;

        private LocalDateTime createdAt;

        private String updatedBy;

        private LocalDateTime updatedAt;

        private ClaimRuleConditionResponse claimRuleCondition;

        @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class ClaimRuleConditionResponse {
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
    private List<AgencyCategories> agencyCategories;
    private PartialWithdrawalAccumulationResponseDto accumulation;
}
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgencyCategories {
        private String categoryId;
        private String categoryName;
        private List<Components> components;
        private List<RefundTypeDTO> refundTypes;

         @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Components{
            private Long componentId;
            private String name;
            private String code;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RefundTypeDTO{
            private Long id;
            private String name;
        }
    }
        
    }
}
