package com.claim.claim_processing.claim.DTO.response.application;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationRentalDeductionResponseDto {

    private Long id;

    // ---------------------------------
    // Deduction Detail
    // ---------------------------------
    private Long deductionDetailId;

    // ---------------------------------
    // Property / Tenant Info
    // ---------------------------------
    private String propertyReferenceId;
    private String tenantOrUnitReference;

    // ---------------------------------
    // Rental Period
    // ---------------------------------
    private LocalDate rentalPeriodFrom;
    private LocalDate rentalPeriodTo;

    // ---------------------------------
    // Rental Amounts
    // ---------------------------------
    private BigDecimal monthlyRentAmount;
    private BigDecimal totalRentDue;
    private BigDecimal totalRentOutstanding;
    private BigDecimal adjustedRentAmount;

    // ---------------------------------
    // Landlord / Agency
    // ---------------------------------
    private String landlordOrAgencyCode;

    // ---------------------------------
    // Rent Clearance Status
    // ---------------------------------
    private Long rentClearanceStatusId;
    private String rentClearanceStatusCode;
    private String rentClearanceStatusName;

    // ---------------------------------
    // Remarks
    // ---------------------------------
    private String remarks;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
