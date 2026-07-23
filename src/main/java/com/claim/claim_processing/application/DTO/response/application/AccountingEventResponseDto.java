package com.claim.claim_processing.application.DTO.response.application;

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
public class AccountingEventResponseDto {

    // Event Information
    private Long id;
    private String eventType;
    private Long claimDetailId;
    private String claimApplicationNumber;

    // Member Information
    private String nppfNumber;
    private String identityNumber;
    private String memberName;

    // Agency Information
    private String agencyCategoryId;
    private String agencyCode;
    private String agencyName;

    // Transaction Information
    private String status;

    // Audit Information
    private String postedBy;
    private LocalDateTime postedAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // Ledger Entries
    private List<LedgerEntryResponseDto> ledgerEntries;

    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class LedgerEntryResponseDto {
    private Long id;
    private Integer seqNo;
    private String mainAccountCode;
    private String mainAccountName;
    private String subAccountCode;
    private String subAccountName;
    private String drcr;
    private BigDecimal amount;
    private String entryRole;
    private String componentCode;
    private String narration;
    private String createdBy;
    private LocalDateTime createdAt;
}
}


