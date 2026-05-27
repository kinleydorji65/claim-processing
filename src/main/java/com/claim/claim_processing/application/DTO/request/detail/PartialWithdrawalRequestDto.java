package com.claim.claim_processing.application.DTO.request.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialWithdrawalRequestDto {

    // ---------------------------------
    // Parent Claim
    // ---------------------------------
    private Long claimApplicationId;

    // ---------------------------------
    // Payee Information
    // ---------------------------------
    private Long payeeTypeId;

    // ---------------------------------
    // Joining Information
    // ---------------------------------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pfJoiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pensionJoiningDate;

    // ---------------------------------
    // Rule / Withdrawal Information
    // ---------------------------------
    private Long partialWithdrawalMasterId;

    private Long withdrawalReasonId;

    private Long withdrawalCauseId;

    private BigDecimal requestedWithdrawalAmount;

    private BigDecimal actualWithdrawalAmount;

    private String reasonDescription;

    // ---------------------------------
    // Unemployment Details
    // ---------------------------------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate unemploymentStartDate;

    private Integer unemploymentDurationMonths;

    // ---------------------------------
    // Disability Details
    // ---------------------------------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate disabilityDate;

    // ---------------------------------
    // Disaster Details
    // ---------------------------------
    private Long disasterTypeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate incidentDate;

    private String placeOfIncident;

    // ---------------------------------
    // Business Details
    // ---------------------------------
    private Long businessTypeId;

    private String businessName;

    private BigDecimal proposedInvestmentAmount;

    // ---------------------------------
    // Housing Details
    // ---------------------------------
    private String housePurchaseType;

    private String propertyLocation;

    private BigDecimal estimatedCost;

    // ---------------------------------
    // Miscellaneous
    // ---------------------------------
    private String description;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;

    private String updatedBy;
}