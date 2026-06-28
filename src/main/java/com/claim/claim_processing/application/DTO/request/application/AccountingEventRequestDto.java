package com.claim.claim_processing.application.DTO.request.application;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingEventRequestDto {

    // Event Information
    private String eventType;
    private Long claimDetailId;

    // Member Information
    private String nppfNumber;
    private String identityNumber;
    private String memberName;

    // Agency Information
    private String agencyCategoryId;
    private String agencyCode;
    private String agencyName;

    // Claim Information
    private Long claimTypeId;
    private String claimTypeName;
    private String claimApplicationNumber;

    // Period Information
    private String monthName;
    private String year;
    private String accountingYear;

    // Transaction Information
    private String tranCode;
    private String narration;
    private String createdBy;

    // Ledger Entries
    private List<LedgerEntryRequestDto> ledgerEntries;
    
    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class LedgerEntryRequestDto {
    private Integer seqNo;
    private String mainAccountCode;
    private String subAccountCode;
    private String drcr;
    private BigDecimal amount;
    private String entryRole;
    private String componentCode;
    private String narration;
    private String createdBy;
}
}


