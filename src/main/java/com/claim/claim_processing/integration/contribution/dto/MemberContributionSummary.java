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
    private BigDecimal rate;

    @JsonProperty("components")
    @JsonAlias({ "componentGroups", "components" })
    private List<ComponentGroup> componentGroups;

    // ========== As-Of-Date Fields ==========
    private LocalDate asOfDate;
    private String currentAccountingYear;

    // ========== Opening Balances ==========
    // PF (Provident Fund) Opening Balances
    private BigDecimal openingPfMc;    // PF Member Contribution
    private BigDecimal openingPfEc;    // PF Employer Contribution
    private BigDecimal openingPfImc;   // PF Interest on Member Contribution
    private BigDecimal openingPfIec;   // PF Interest on Employer Contribution
    
    // P (Pension) Opening Balances
    private BigDecimal openingPMc;     // P Member Contribution
    private BigDecimal openingPEc;     // P Employer Contribution
    private BigDecimal openingPImc;    // P Interest on Member Contribution
    private BigDecimal openingPIec;    // P Interest on Employer Contribution

    // ================================================================
    // ✅ EXCESS SERVICE - Using ExcessServiceResultDto
    // ================================================================
    private ExcessServiceResultDto excessService;

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

    // ========== Helper Methods ==========
    
    public boolean hasExcessService() {
        return excessService != null 
                && excessService.isEligible() 
                && excessService.getTotalExcessAmount() != null
                && excessService.getTotalExcessAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalPayableWithExcess() {
        BigDecimal total = totalBalance != null ? totalBalance : BigDecimal.ZERO;
        if (hasExcessService() && excessService.getTotalExcessAmount() != null) {
            total = total.add(excessService.getTotalExcessAmount());
        }
        return total;
    }

    public boolean isAsOfCurrentDate() {
        return asOfDate != null && asOfDate.equals(LocalDate.now());
    }
    
    public String getExcessSummary() {
        if (!hasExcessService()) {
            return "No excess service";
        }
        return String.format("Excess service: %.2f from %s to %s",
            excessService.getTotalExcessAmount(),
            excessService.getExcessStartDate(),
            excessService.getExcessEndDate());
    }
}