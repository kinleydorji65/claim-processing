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

    private Long payeeTypeId;

    private Long withdrawalReasonId;

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
    // Audit
    // ---------------------------------
    private String createdBy;

    private String updatedBy;
}