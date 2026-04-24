package com.claim.claim_processing.common.DTO.others.member;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBankResponseDto {

    private Long id;

    // -------------------------------
    // ACCOUNT DETAILS
    // -------------------------------
    private String accountNumber;
    private String accountHolderName;
    private String accountType;

    // -------------------------------
    // BANK INFO
    // -------------------------------
    private Long bankId;
    private String bankName; // optional (from master)

    // -------------------------------
    // DEFAULT FLAG
    // -------------------------------
    private Boolean isDefault;

    // -------------------------------
    // MEMBER (FLATTENED)
    // -------------------------------
    private String memberCode;
    private String memberName; // optional (if needed for UI)
}
