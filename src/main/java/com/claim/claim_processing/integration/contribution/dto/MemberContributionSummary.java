package com.claim.claim_processing.integration.contribution.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class MemberContributionSummary {

    // ========== Existing Fields ==========
    private String nppfNumber;
    private Long schemeTypeId;
    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private Integer totalContributionYears;
    private LocalDate contributionEndDate;
    private BigDecimal totalPrincipalAmount;
    private BigDecimal totalInterestAmount;
    private BigDecimal totalBalance;

    @JsonProperty("components")
    @JsonAlias({ "componentGroups", "components" })
    private List<ComponentGroup> componentGroups;

    // ========== NEW: Excess Service Fields ==========
    private BigDecimal excessServiceAmount;
    private LocalDate cutoffServiceDate;
    private Integer cutoffYears;
    private LocalDate excessStartDate;
    private LocalDate excessEndDate;
    private Integer totalEOLMonths;
    private Integer eolMonthsInExcess;
    private BigDecimal totalContributionsInExcess;
    private BigDecimal totalInterestInExcess;
    private String excessStatus;
    private String excessMessage;
    private List<ExcessYearDetail> excessYearDetails;
    private List<ExcessMonthlyDetail> excessMonthlyDetails;

    // ========== Inner Classes ==========
    @Data
    @Builder
    public static class ComponentGroup {
        private String componentCode;
        private String componentName;
        private BigDecimal principalAmount;
        private BigDecimal interestAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    public static class ExcessYearDetail {
        private String accountingYear;
        private String yearType;
        private BigDecimal openingBalance;
        private BigDecimal interestOnOpening;
        private BigDecimal duringTheYear;
        private BigDecimal closingBalance;
        private BigDecimal interestRate;
        private LocalDate interestDate;
        private Integer daysInYear;
        private Integer eolMonthsInYear;
        private BigDecimal yearlyContributions;
        private BigDecimal yearlyInterest;
        private List<ExcessMonthlyDetail> monthlyDetails;
    }

    @Data
    @Builder
    public static class ExcessMonthlyDetail {
        private String dueMonth;
        private LocalDate invoiceDate;
        private BigDecimal mpc;
        private BigDecimal epc;
        private BigDecimal totalPension;
        private Integer days;
        private BigDecimal interest;
        private BigDecimal cPlusI;
        private boolean isEOL;
    }

    // ========== Helper Methods ==========
    public boolean hasExcessService() {
        return excessServiceAmount != null
                && excessServiceAmount.compareTo(BigDecimal.ZERO) > 0
                && "CALCULATED".equals(excessStatus);
    }

    public BigDecimal getTotalPayableWithExcess() {
        BigDecimal total = totalBalance != null ? totalBalance : BigDecimal.ZERO;
        if (hasExcessService()) {
            total = total.add(excessServiceAmount);
        }
        return total;
    }
}