package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimBankResponseDto {
    private Long id;

    // ---------------------------------
    // Beneficiary
    // ---------------------------------
    private String beneficiaryIdentifier;

    private Long claimantTypeId;
    private String claimantTypeName;

    // ---------------------------------
    // Bank Info
    // ---------------------------------
    private Long bankTypeId;
    private String bankTypeName;

    private String accountNumber;
    private String accountHolderName;
    private String ifscOrRoutingCode;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isDefaultBank;

    private String verifiedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;

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
