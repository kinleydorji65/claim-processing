package com.claim.claim_processing.integration.contribution.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemitanceRecalculationResponse {
    
    // Member Information
    private String nppfNumber;
    private String targetYear;
    private List<RecalculatedMonthsList> recalculatedMonthsList;
    
    // Summary
    private BigDecimal totalRecalculatedContributions;
    private BigDecimal totalRecalculatedInterest;
    private BigDecimal totalRecalculatedAmount;
    
    // Calculation Details
    private BigDecimal appliedInterestRate;
    private Integer yearBasis;
    private LocalDate calculationDate;
    
    // Status
    private String status;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecalculatedMonthsList  {
        private String month;
        // Opening Balances for the target year (from previous year closing)
        private OpeningBalanceDto openingBalances;
        // Recalculated Months (only the selected months)
        private List<RecalculatedMonthDto> recalculatedMonths;
        
        // Closing Balances after processing selected months
        private ClosingBalanceDto closingBalances;

    }
    
    // ========== INNER DTOs ==========
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningBalanceDto {
        private String year;
        
        // PF Components
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        
        // Pension Components
        private BigDecimal pMc;
        private BigDecimal pEc;
        private BigDecimal pImc;
        private BigDecimal pIec;
        
        // Other Components
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal ivc;
        private BigDecimal igc;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecalculatedMonthDto {
        private String month;
        private String monthName;
        private LocalDate invoiceDate;
        private Integer daysForInterest;
        private BigDecimal interestRate;
        
        // PF Components
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        
        // Pension Components
        private BigDecimal pMc;
        private BigDecimal pEc;
        private BigDecimal pImc;
        private BigDecimal pIec;
        
        // Other Components
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal ivc;
        private BigDecimal igc;
        
        // Totals for this month
        private BigDecimal totalContribution;
        private BigDecimal totalInterest;
        private BigDecimal totalAmount;
        private String status; // "RECALCULATED" or "EOL"
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClosingBalanceDto {
        // PF Components
        private BigDecimal pfMc;
        private BigDecimal pfEc;
        private BigDecimal pfImc;
        private BigDecimal pfIec;
        
        // Pension Components
        private BigDecimal pMc;
        private BigDecimal pEc;
        private BigDecimal pImc;
        private BigDecimal pIec;
        
        // Other Components
        private BigDecimal gc;
        private BigDecimal vc;
        private BigDecimal ivc;
        private BigDecimal igc;
    }
}
