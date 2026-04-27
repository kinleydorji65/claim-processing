package com.claim.claim_processing.claim.DTO.detail;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormalClaimResponseDto {

    private Long id;

    // ---------- Parent ----------
    private Long claimApplicationId;

    // ---------- Masters (flattened) ----------
    private Long cessationTypeId;
    private String cessationTypeName;

    private Long payeeTypeId;
    private String payeeTypeName;

    private Long terminationReasonTypeId;
    private String terminationReasonTypeName;

    // ---------- Dates ----------
    private LocalDate dateOfTermination;
    private LocalDate pfJoiningDate;
    private LocalDate pensionJoiningDate;

    private LocalDate relievingOrderDate;
    private LocalDate cessationEffectiveDate;
    private LocalDate exitDate;

    private LocalDate lastPayCertificateDate;
    private LocalDate auditClearanceDate;
    private LocalDate dateOfServiceJoining;

    // ---------- Business Fields ----------
    private String terminatedBy;
    private String terminationRemarks;

    private String relievingOrderNumber;

    private String lastPayCertificateNumber;
    private String auditClearanceNumber;

    private BigDecimal finalBasicSalary;

    private Integer nonContributionMonths;

    private String remarks;

    // ---------- Audit ----------
    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
