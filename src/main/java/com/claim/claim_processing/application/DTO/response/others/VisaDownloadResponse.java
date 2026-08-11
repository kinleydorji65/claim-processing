package com.claim.claim_processing.application.DTO.response.others;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaDownloadResponse {
    
    // ============ VISA DOWNLOADED FIELDS ============
    private Long id;
    private String location;
    private Long submissionChannelId;
    private String submissionChannelName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime downloadedDate;
    
    private String deviceName;
    private String ipAddress;
    private String nppfNumber;
    private String cid;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String createdBy;

    // ============ NESTED VISA RESPONSE DATA ============
    private VisaResponseDataResponse visaResponseDataResponse;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisaResponseDataResponse {
        
        private Long id;
        private Long visaDownloadedId;  // FK to parent
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime joiningDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime calculationDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime firstContributionDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastContributionDate;
        
        private BigDecimal totalPrincipal;
        private BigDecimal totalInterest;
        private BigDecimal totalBalance;
        private String currentYear;
        private BigDecimal currentYearRate;
        private Integer currentYearBasis;
        private String status;
        private String responseMessage;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        
        private String createdBy;
        
        // Nested financial year data - CORRECTED FIELD NAME
        private VisaFinancialYearDataResponse visaFinancialYearDataResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisaFinancialYearDataResponse {
        
        private Long id;
        private Long visaResponseDataId;  // FK to parent
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
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }
}