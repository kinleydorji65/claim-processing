package com.claim.claim_processing.application.DTO.response.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialWithdrawalResponseDto {

    private Long id;

    private Long claimApplicationId;
    private String applicationNumber;

    private Long claimDetailId;

    private Long payeeTypeId;
    private String payeeTypeName;

    private Long withdrawalReasonId;
    private String withdrawalReasonName;

    private BigDecimal requestedWithdrawalAmount;
    private BigDecimal actualWithdrawalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate unemploymentStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate disabilityDate;

    private Long unemploymentCauseId;
    private String unemploymentCauseCode;
    private String unemploymentCauseName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate incidentDate;

    private String placeOfIncident;

    private Long businessTypeId;
    private String businessTypeName;

    private String businessName;
    private BigDecimal proposedInvestmentAmount;

    private String housePurchaseType;
    private String propertyLocation;
    private BigDecimal estimatedCost;

    private String description;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}