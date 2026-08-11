package com.claim.claim_processing.application.DTO.request.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO.WrongRemittanceForfeitedRequestDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongRemitanceRequestDTO {
    private Long id;

    // ===== FOREIGN KEY TO CLAIM APPLICATION =====
    private Long applicationId;

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

    // ===== RECALCULATION TOTALS =====
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

    

    private List<WrongRemittanceRecalculatedMonthRequestDTO> recalculateMonths;
    private List<WrongRemittanceForfeitedRequestDTO> forfeitedRequest;
    private List<WrongRemittanceCalculationComponentRequestDTO> components;

    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class WrongRemittanceRecalculatedMonthRequestDTO {

    private Long id;
    private Long wrongRemitanceId;
    private String month;

    private String monthName;
    private LocalDate invoiceDate;

    private BigDecimal daysForInterest;
    private BigDecimal interestRate;

    // ===== PF COMPONENTS ====
    private BigDecimal pfMc;
    private BigDecimal pfEc;
    private BigDecimal pfImc;
    private BigDecimal pfIec;

    // ===== PENSION COMPONENTS ====
    private BigDecimal pMc;
    private BigDecimal pEc;
    private BigDecimal pImc;
    private BigDecimal pIec;

    // ===== GRATUITY COMPONENTS ====
    private BigDecimal gc;
    private BigDecimal gic;

    // ===== VOLUNTARY COMPONENTS ====
    private BigDecimal vc;
    private BigDecimal vic;

    // ===== INTEREST ON VOLUNTARY & GRATUITY ====
    private BigDecimal ivc;
    private BigDecimal igc;

    // ===== TOTALS ====
    private BigDecimal totalContribution;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;

    private String status;

    // ===== AUDIT FIELDS (Optional) =====
    private String createdBy;
    private String updatedBy;
}
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrongRemittanceForfeitedRequestDTO {

        private Long id;
        private Long wrongRemittanceId;
        private String componentCode;

        private String componentName;

        private String componentType; // FORFEITED
        private BigDecimal amount;

        // Optional fields for creation
        private String createdBy;
        private String updatedBy;

    }

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class WrongRemittanceCalculationComponentRequestDTO {

    private Long id;
    private Long componentMasterId;
    private String componentCode;
    private BigDecimal amount;

    // Audit fields (optional)
    private String createdBy;
    private String updatedBy;
} 
}
