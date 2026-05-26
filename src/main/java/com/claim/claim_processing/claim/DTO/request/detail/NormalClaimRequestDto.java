package com.claim.claim_processing.claim.DTO.request.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormalClaimRequestDto {

    // ---------- Parent ----------
    private Long claimApplicationId;

    // ---------- Masters ----------
    private Long cessationTypeId;

    private Long payeeTypeId;

    private Long terminationReasonTypeId;

    // ---------- Always shown ----------
    private String lastPayMonth;

    // ---------- Termination fields ----------
    private LocalDate dateOfTermination;

    private String terminatedBy;

    private String terminationRemarks;

    // ---------- Retirement-like fields ----------
    private String relievingOrderNumber;

    private LocalDate relievingOrderDate;

    // ---------- Exit-like fields ----------
    private String relievingReferenceNumber;


    // ---------- Common field ----------
    private LocalDate cessationEffectiveDate;

    // ---------- Backend/display-related fields ----------
    private LocalDate dateOfServiceJoining;

    private LocalDate pfJoiningDate;

    private LocalDate pensionJoiningDate;

    private BigDecimal finalBasicSalary;

    private Integer nonContributionMonths;

    private String remarks;

    // ---------- Audit ----------
    private String createdBy;

    private String updatedBy;
}