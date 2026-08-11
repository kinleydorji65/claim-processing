package com.claim.claim_processing.application.DTO.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemitanceResponseDTO {

    // ===== ID =====
    private Long id;

    // ===== FOREIGN KEY TO CLAIM APPLICATION =====
    private Long applicationId;
    private String applicationNumber; // From ClaimApplication
    private String categoryId; // From ClaimApplication

    // ===== MEMBER INFORMATION =====
    private String nppfNumber;
    private String memberName;
    private String targetYear;

    // ===== OPENING BALANCES =====
    // PF (PROVIDENT FUND)
    private BigDecimal openingPfMc;
    private BigDecimal openingPfEc;
    private BigDecimal openingPfImc;
    private BigDecimal openingPfIec;

    // P (PENSION)
    private BigDecimal openingPMc;
    private BigDecimal openingPEc;
    private BigDecimal openingPImc;
    private BigDecimal openingPIec;

    // G (GRATUITY)
    private BigDecimal openingGc;
    private BigDecimal openingGic;

    // V (VOLUNTARY)
    private BigDecimal openingVc;
    private BigDecimal openingVic;

    // I (INTEREST ON VOLUNTARY & GRATUITY)
    private BigDecimal openingIvc;
    private BigDecimal openingIgc;

    // ===== CLOSING BALANCES =====
    // PF (PROVIDENT FUND)
    private BigDecimal closingPfMc;
    private BigDecimal closingPfEc;
    private BigDecimal closingPfImc;
    private BigDecimal closingPfIec;

    // P (PENSION)
    private BigDecimal closingPMc;
    private BigDecimal closingPEc;
    private BigDecimal closingPImc;
    private BigDecimal closingPIec;

    // G (GRATUITY)
    private BigDecimal closingGc;
    private BigDecimal closingGic;

    // V (VOLUNTARY)
    private BigDecimal closingVc;
    private BigDecimal closingVic;

    // I (INTEREST ON VOLUNTARY & GRATUITY)
    private BigDecimal closingIvc;
    private BigDecimal closingIgc;

    // ===== TOTALS =====
    private BigDecimal openingBalanceTotal;
    private BigDecimal closingBalanceTotal;
    private BigDecimal totalRecalculatedContributions;
    private BigDecimal totalRecalculatedInterest;
    private BigDecimal totalRecalculatedAmount;

    // ===== CONFIGURATION =====
    private Boolean withInterest;
    private BigDecimal appliedInterestRate;
    private Integer yearBasis;
    private LocalDate calculationDate;

    // ===== SELECTED MONTHS =====
    private List<String> selectedMonths;
    private Integer selectedMonthCount;

    // ===== STATUS AND METADATA =====
    private String status;
    private String message;
    private Integer yearsProcessed;
    private String fromYear;
    private String toYear;

    // ===== AUDIT =====
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    private List<WrongRemittanceRecalculatedMonthResponseDTO> recalculateMonths;
    private List<WrongRemittanceCalculationComponentResponseDTO> components;
    private List<WrongRemittanceForfeitedResponseDTO> wrongRemitanceForfeiteds;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class WrongRemittanceRecalculatedMonthResponseDTO {

    private Long id;
    private Long wrongRemitanceId;

    // ===== MONTH INFORMATION =====
    private String month;
    private String monthName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime invoiceDate;

    private BigDecimal daysForInterest;
    private BigDecimal interestRate;

    // ===== PF COMPONENTS =====
    private BigDecimal pfMc;
    private BigDecimal pfEc;
    private BigDecimal pfImc;
    private BigDecimal pfIec;

    // ===== PENSION COMPONENTS =====
    private BigDecimal pMc;
    private BigDecimal pEc;
    private BigDecimal pImc;
    private BigDecimal pIec;

    // ===== GRATUITY COMPONENTS =====
    private BigDecimal gc;
    private BigDecimal gic;

    // ===== VOLUNTARY COMPONENTS =====
    private BigDecimal vc;
    private BigDecimal vic;

    // ===== INTEREST ON VOLUNTARY & GRATUITY =====
    private BigDecimal ivc;
    private BigDecimal igc;

    // ===== TOTALS =====
    private BigDecimal totalContribution;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;

    // ===== STATUS =====
    private String status;

    // ===== AUDIT FIELDS =====
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrongRemittanceForfeitedResponseDTO {

        private Long id;
        private Long wrongRemittanceId;
        private String componentCode;
        private String componentName;
        private BigDecimal amount;
        private String createdBy;
        private String updatedBy;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrongRemittanceCalculationComponentResponseDTO {

        private Long id;
        private String componentCode;
        private String componentName;
        private BigDecimal amount;
        private String createdBy;
        private String updatedBy;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }
}
