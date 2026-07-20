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
public class NormalClaimResponseDto {

    private Long id;

    // Parent
    private Long claimApplicationId;
    private String applicationNumber;

    private Long claimDetailId;

    // Masters
    private Long cessationTypeId;
    private String cessationTypeName;

    private Long payeeTypeId;
    private String payeeTypeName;

    private Long terminationReasonTypeId;
    private String terminationReasonTypeName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cessationEffectiveDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfServiceJoining;

    // Business Fields
    private String terminatedBy;

    private String terminationRemarks;

    private String relievingOrderNumber;

    private String relievingReferenceNumber;

    private String lastPayMonth;

    private BigDecimal finalBasicSalary;

    private String remarks;

    // Audit
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}