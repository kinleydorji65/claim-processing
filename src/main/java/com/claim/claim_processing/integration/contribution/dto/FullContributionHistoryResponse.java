package com.claim.claim_processing.integration.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullContributionHistoryResponse {

    private String nppfNumber;
    private String memberName;
    private LocalDate joiningDate;
    private LocalDate calculationDate;
    private LocalDate firstContributionDate;
    private LocalDate lastContributionDate;

    // Financial Year wise data (like the image format)
    private List<FinancialYearData> financialYearData;

    // Current year monthly breakdown
    private List<MonthlyContributionDetail> currentYearMonthlyDetails;

    // Final totals
    private BigDecimal totalPrincipal;
    private BigDecimal totalInterest;
    private BigDecimal totalBalance;

    // Current year info
    private String currentYear;
    private BigDecimal currentYearRate;
    private Integer currentYearBasis;

    // Excess service info (if applicable)
    private ExcessServiceInfo excessService;

    private String status;
    private String message;

    // ========== INNER CLASS: Financial Year Data ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialYearData {
        private String financialYear; // e.g., "2013-2014"

        // ===== OPENING BALANCES =====
        private OpeningBalances openingBalances;

        // ===== TRANSACTION DURING THE YEAR =====
        private TransactionDuringYear transactionDuringYear;

        // ===== EXCESS TRANSFERRED FROM PENSION TO PF =====
        private ExcessTransferred excessTransferred;

        // ===== CLOSING BALANCES =====
        private ClosingBalances closingBalances;
    }

    // ========== OPENING BALANCES ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningBalances {
        // PF Components
        private BigDecimal pfMc;      // PF Member Contribution
        private BigDecimal pfEc;      // PF Employer Contribution
        private BigDecimal pfImc;     // PF Interest on Member Contribution
        private BigDecimal pfIec;     // PF Interest on Employer Contribution
        private BigDecimal pfTotal;   // Total PF

        // PC/Pension Components
        private BigDecimal pcMc;      // Pension Member Contribution
        private BigDecimal pcEc;      // Pension Employer Contribution
        private BigDecimal pcImc;     // Pension Interest on Member Contribution
        private BigDecimal pcIec;     // Pension Interest on Employer Contribution
        private BigDecimal pcTotal;   // Total Pension

        // Grand Total Opening
        private BigDecimal grandTotal;
    }

    // ========== TRANSACTION DURING THE YEAR ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDuringYear {
        // PF Components
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        private BigDecimal pfTotal;

        // PC/Pension Components
        private BigDecimal pcMc;
        private BigDecimal pcEc;
        private BigDecimal pcImc;
        private BigDecimal pcIec;
        private BigDecimal pcTotal;

        // Grand Total Transaction
        private BigDecimal grandTotal;
    }

    // ========== EXCESS TRANSFERRED ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcessTransferred {
        // PC/Pension Components transferred to PF
        private BigDecimal pcMc;
        private BigDecimal pcEc;
        private BigDecimal pcImc;
        private BigDecimal pcIec;
        private BigDecimal total;
    }

    // ========== CLOSING BALANCES ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClosingBalances {
        // PF Components
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        private BigDecimal pfTotal;

        // PC/Pension Components
        private BigDecimal pcMc;
        private BigDecimal pcEc;
        private BigDecimal pcImc;
        private BigDecimal pcIec;
        private BigDecimal pcTotal;

        // Grand Total Closing
        private BigDecimal grandTotal;
    }

    // ========== MONTHLY CONTRIBUTION DETAIL ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyContributionDetail {
        private String month;
        private String monthName;
        private Integer monthNumber;
        private LocalDate contributionDate;
        private Integer daysSinceContribution;
        private BigDecimal interestRate;
        private Boolean hasContribution;

        // Contribution amounts
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pMc;
        private BigDecimal pEc;
        private BigDecimal gc;
        private BigDecimal vc;

        // Interest on this month's contribution
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        private BigDecimal pImc;
        private BigDecimal pIec;
        private BigDecimal gic;
        private BigDecimal vic;

        // Running balance after this month
        private BigDecimal runningPfMc;
        private BigDecimal runningPfEc;
        private BigDecimal runningPfImc;
        private BigDecimal runningPfIec;
        private BigDecimal runningPMc;
        private BigDecimal runningPEc;
        private BigDecimal runningPImc;
        private BigDecimal runningPIec;
        private BigDecimal runningGc;
        private BigDecimal runningVc;

        // Month totals
        private BigDecimal monthContribution;
        private BigDecimal monthInterest;
        private BigDecimal monthTotal;
        private BigDecimal cumulativeTotal;
    }

    // ========== EXCESS SERVICE INFO ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcessServiceInfo {
        private Boolean isEligible;
        private LocalDate excessStartDate;
        private LocalDate excessEndDate;
        private LocalDate cutoffServiceDate;
        private Integer cutoffYears;
        private BigDecimal totalExcessAmount;
        private BigDecimal totalContributionsInExcess;
        private BigDecimal totalInterestInExcess;
        private Integer totalEOLMonths;
        private Integer eolMonthsInExcess;
        private String status;
        private String message;
    }
}