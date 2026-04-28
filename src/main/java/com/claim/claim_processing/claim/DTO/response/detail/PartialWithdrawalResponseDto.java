package com.claim.claim_processing.claim.DTO.response.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialWithdrawalResponseDto {

    private Long id;

    // ---------- Parent ----------
    private Long claimApplicationId;

    // ---------- Masters ----------
    private Long payeeTypeId;
    private String payeeTypeName;

    private Long partialWithdrawalMasterId;

    private Long withdrawalReasonId;
    private String withdrawalReasonName;

    private Long withdrawalCauseId;
    private String withdrawalCauseName;

    // ---------- Dates ----------
    private LocalDate pfJoiningDate;
    private LocalDate pensionJoiningDate;

    // ---------- Amounts ----------
    private BigDecimal requestedWithdrawalAmount;
    private BigDecimal actualWithdrawalAmount;

    // ---------- Common ----------
    private String reasonDescription;

    // ======================================================
    // 🟡 UNEMPLOYMENT SECTION
    // ======================================================
    private LocalDate unemploymentStartDate;
    private Integer unemploymentDurationMonths;

    // ======================================================
    // 🟡 DISABILITY SECTION
    // ======================================================
    private LocalDate disabilityDate;

    // ======================================================
    // 🟡 DISASTER SECTION
    // ======================================================
    private Long disasterTypeId;
    private String disasterTypeName;

    private LocalDate incidentDate;
    private String placeOfIncident;

    // ======================================================
    // 🟡 BUSINESS SECTION
    // ======================================================
    private Long businessTypeId;
    private String businessTypeName;

    private String businessName;
    private BigDecimal proposedInvestmentAmount;

    // ======================================================
    // 🟡 HOUSING SECTION
    // ======================================================
    private String housePurchaseType;
    private String propertyLocation;
    private BigDecimal estimatedCost;

    // ---------- Extra ----------
    private String description;

    // ---------- Audit ----------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
