package com.claim.claim_processing.rule.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.claim.claim_processing.rule.ruleProcessing.dto.RuleResponseDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimPreviewResponseDto {

    private RequestDetail requestDetail;
    private ContributionDetail contributionDetail;
    private List<RuleResponseDto> rules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDetail {
        private Long claimTypeId;
        private String nppfNumber;
        private Long circumtancesId;
        private String memberCategoryId;
        private LocalDate cessationDate;
        private LocalDate serviceJoiningDate;
        private Boolean isSpecialCase;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContributionDetail {
        private String nppfNumber;
        private Long schemeTypeId;

        private LocalDate pfJoiningDate;
        private LocalDate pensionJoiningDate;

        private Integer totalContributionMonths;
        private Integer totalNonContributionMonths;
        private Integer totalContributionYears;

        private LocalDate contributionStartDate;
        private LocalDate contributionEndDate;

        private BigDecimal totalBalance;

        private List<ComponentBalance> componentGroups;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentBalance {
        private String code;
        private String name;
        private BigDecimal principal;
        private BigDecimal interest;
        private BigDecimal totalBalance;
        private BigDecimal interestRate;
        private LocalDate lastInterestDate;
        private LocalDate lastUpdatedDate;
    }
}
