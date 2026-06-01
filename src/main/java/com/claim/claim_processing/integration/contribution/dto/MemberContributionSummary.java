package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MemberContributionSummary {

    private String nppfNumber;
    private Long schemeTypeId;

    private LocalDate pfJoiningDate;
    private LocalDate pensionJoiningDate;

    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private Integer totalContributionYears;

    private LocalDate contributionStartDate;
    private LocalDate contributionEndDate;

    private BigDecimal totalPrincipalAmount;
    private BigDecimal totalInterestAmount;
    private BigDecimal totalBalance;

    private List<ComponentGroup> componentGroups;

    @Data
    @Builder
    public static class ComponentGroup {

        private String componentCode;
        private String componentName;

        private BigDecimal principalAmount;
        private BigDecimal interestAmount;
        private BigDecimal totalAmount;
    }
}