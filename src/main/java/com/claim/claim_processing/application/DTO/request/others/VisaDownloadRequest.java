package com.claim.claim_processing.application.DTO.request.others;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaDownloadRequest {
    
    // ============ VISA DOWNLOADED FIELDS ============
    private String location;
    private Long submissionChannelId;
    private String ipAddress;
    private String nppfNumber;
    private String cid;
    private String createdBy;

    // ============ NESTED VISA RESPONSE DATA ============
    private VisaResponseDataRequest visaResponseDataRequest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisaResponseDataRequest {
        
        private LocalDateTime joiningDate;
        private LocalDateTime calculationDate;
        private LocalDateTime firstContributionDate;
        private LocalDateTime lastContributionDate;
        private BigDecimal totalPrincipal;
        private BigDecimal totalInterest;
        private BigDecimal totalBalance;
        private String currentYear;
        private BigDecimal currentYearRate;
        private Integer currentYearBasis;
        private String status;
        private String responseMessage;
        private String createdBy;
        
        // Nested financial year data
        private VisaFinancialYearDataRequest financialYearDataRequest;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisaFinancialYearDataRequest {
        
        private String financialYear;

        // ============ OPENING BALANCES ===========
        private BigDecimal openingPfMc;
        private BigDecimal openingPfEc;
        private BigDecimal openingPfImc;
        private BigDecimal openingPfIec;
        private BigDecimal openingPfTotal;
        private BigDecimal openingPcMc;
        private BigDecimal openingPcEc;
        private BigDecimal openingPcImc;
        private BigDecimal openingPcIec;
        private BigDecimal openingPcTotal;
        private BigDecimal openingGrandTotal;

        // ============ TRANSACTION DURING YEAR ===========
        private BigDecimal transactionPfMc;
        private BigDecimal transactionPfEc;
        private BigDecimal transactionPfImc;
        private BigDecimal transactionPfIec;
        private BigDecimal transactionPfTotal;
        private BigDecimal transactionPcMc;
        private BigDecimal transactionPcEc;
        private BigDecimal transactionPcImc;
        private BigDecimal transactionPcIec;
        private BigDecimal transactionPcTotal;
        private BigDecimal transactionGrandTotal;

        // ============ EXCESS TRANSFERRED ===========
        private BigDecimal excessPcMc;
        private BigDecimal excessPcEc;
        private BigDecimal excessPcImc;
        private BigDecimal excessPcIec;
        private BigDecimal excessTotal;

        // ============ CLOSING BALANCES ===========
        private BigDecimal closingPfMc;
        private BigDecimal closingPfEc;
        private BigDecimal closingPfImc;
        private BigDecimal closingPfIec;
        private BigDecimal closingPfTotal;
        private BigDecimal closingPcMc;
        private BigDecimal closingPcEc;
        private BigDecimal closingPcImc;
        private BigDecimal closingPcIec;
        private BigDecimal closingPcTotal;
        private BigDecimal closingGrandTotal;
    }
}