package com.claim.claim_processing.rule.ruleGateWay.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedSubClaimRuleDto {

    private Long subClaimMappingId;

    private String subClaimCode;
    private String subClaimType;
    private String subClaimDescription;

    private String ruleCode;
    private String ruleName;
    private String ruleEffect;

    private CategoryScheme categoryScheme;
    private Condition condition;
    private TimeIndication timeIndication;
    private ComponentMapping componentMapping;

    private BigDecimal withdrawalPercentage;
    private String refundTypeName;
    private boolean isRefundEligible;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryScheme {
        private Long categoryId;
        private String categoryName;
        private Long schemeTypeId;
        private String schemeTypeName;
        private String categoryCode;
        private String schemeCode;
        private String categorySchemeCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition {
        private Long id;
        private String conditionCode;
        private String conditionCheck;
        private String expression;
        private Long duration;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeIndication {
        private Long id;
        private String timeIndicationCode;
        private String timeIndication;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentMapping {
        private Long id;
        private String componentMappingCode;

        private String hasPf;
        private String hasPc;
        private String hasEc;
        private String hasMc;
        private String hasImc;
        private String hasIec;
        private String hasGc;
        private String hasGic;
        private String hasVc;
        private String hasVic;

        private List<ComponentExpression> expressions;

        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentExpression {
        private Long id;
        private String componentMappingCode;
        private String expression;
    }
}