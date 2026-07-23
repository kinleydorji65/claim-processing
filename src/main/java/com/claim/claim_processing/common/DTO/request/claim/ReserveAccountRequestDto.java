package com.claim.claim_processing.common.DTO.request.claim;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveAccountRequestDto {

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
}