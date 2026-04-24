package com.claim.claim_processing.common.DTO.others.agency;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyBankResponseDto {

    private Long id;

    // -------------------------------
    // ACCOUNT DETAILS
    // -------------------------------
    private String accountNumber;
    private String accountHolderName;
    private String accountType;

    private String swiftCode;

    // -------------------------------
    // BANK INFO
    // -------------------------------
    private Long bankId;
    private String bankName; // optional (if you fetch from master)

    private Long currencyCodeId;
    private String currencyCode; // optional (BTN, USD, etc.)

    // -------------------------------
    // DEFAULT FLAG
    // -------------------------------
    private Boolean isDefault;

    // -------------------------------
    // AGENCY (FLATTENED)
    // -------------------------------
    private String agencyCode;
    private String agencyName; // optional (if needed for UI)
}
