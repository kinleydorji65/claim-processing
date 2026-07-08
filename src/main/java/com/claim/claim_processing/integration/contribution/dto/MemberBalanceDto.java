package com.claim.claim_processing.integration.contribution.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberBalanceDto {

    private String cid;
    private String nppfNumber;
    private String agencyCode;
    private String agencyCategoryId;

    /** Null = all-time; populated for single-year queries */
    private String accountingYear;

    /** SUM of all bifurcated contribution components */
    private BigDecimal totalContributions;

    /** SUM of credited interest (zero if includeInterest=false or none exists) */
    private BigDecimal totalInterest;

    /** totalContributions + totalInterest */
    private BigDecimal totalBalance;

    /** Contribution breakdown by component (IEC/IMC/IPC/IGC/IVC mapped from bif fields) */
    private List<ComponentBalance> contributionsByComponent;

    /**
     * Interest breakdown by component from MIR rows.
     * Null/empty when includeInterest=false.
     */
    private List<InterestByComponent> interestByComponent;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComponentBalance {
        private String componentCode;
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InterestByComponent {
        private String componentCode;
        private String accountingYear;
        private BigDecimal openingBalance;
        private BigDecimal contributionsYtd;
        private BigDecimal totalInterest;
        private BigDecimal closingBalance;
        private String status;
    }
}
