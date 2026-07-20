package com.claim.claim_processing.application.DTO.request.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormalClaimRequestDto {

    private Long normalClaimId;
    // ---------- Masters ----------
    private Long cessationTypeId;

    private Long payeeTypeId;

    private Long terminationReasonTypeId;

    // ---------- Always shown ----------
    private String lastPayMonth;


    private String terminatedBy;

    private String terminationRemarks;

    // ---------- Retirement-like fields ----------
    private String relievingOrderNumber;

    // ---------- Exit-like fields ----------
    private String relievingReferenceNumber;


    // ---------- Common field ----------
    private LocalDate cessationEffectiveDate;

    // ---------- Backend/display-related fields ----------
    private LocalDate dateOfServiceJoining;

    private BigDecimal finalBasicSalary;

    private String remarks;

    // ---------- Audit ----------
    private String createdBy;

    private String updatedBy;
}