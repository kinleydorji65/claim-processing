package com.claim.claim_processing.common.DTO.response.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveAccountResponseDto {

    private Long id;

    // -------------------------------
    // MEMBER INFORMATION
    // -------------------------------
    private String memberCode;
    private String nppfNumber;
    private String identityNumber;

    // -------------------------------
    // AGENCY INFORMATION
    // -------------------------------
    private String agencyCategoryId;
    private String agencyCode;

    // -------------------------------
    // RESERVE ACCOUNT DETAILS
    // -------------------------------
    private String reserveType;

    // -------------------------------
    // AMOUNT DETAILS
    // -------------------------------
    private BigDecimal totalAmount;
    private BigDecimal forfeitedAmount;
    private String componentCode;

    // -------------------------------
    // STATUS
    // -------------------------------
    private String status;
    private LocalDateTime releaseDate;
    private String releasedBy;
    private String releaseReference;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private String isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}