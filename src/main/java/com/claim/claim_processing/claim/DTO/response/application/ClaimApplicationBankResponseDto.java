package com.claim.claim_processing.claim.DTO.response.application;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationBankResponseDto {

    private Long id;

    // ---------------------------------
    // Parent Claim Application
    // ---------------------------------
    private Long claimApplicationId;

    // ---------------------------------
    // Beneficiary
    // ---------------------------------
    private String beneficiaryIdentifier;

    private Long claimantTypeId;
    private String claimantTypeCode;
    private String claimantTypeName;

    // ---------------------------------
    // Bank Info
    // ---------------------------------
    private Long bankTypeId;
    private String bankTypeCode;
    private String bankTypeName;

    private String accountNumber;
    private String accountHolderName;
    private String ifscOrRoutingCode;

    // ---------------------------------
    // Flags
    // ---------------------------------
    private ActivityEnum isDefaultBank;
    private ActivityEnum isSelectedBank;
    private ActivityEnum isActive;

    // ---------------------------------
    // Verification Status
    // ---------------------------------
    private Long bankVerificationStatusId;
    private String bankVerificationStatusCode;
    private String bankVerificationStatusName;

    private String verifiedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;

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
