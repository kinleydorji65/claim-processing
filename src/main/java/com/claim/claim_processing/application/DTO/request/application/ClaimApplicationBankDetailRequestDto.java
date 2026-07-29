package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationBankDetailRequestDto {
    private Long id;
    private String beneficiaryIdentifier;

    private Long relationTypeId;
    
    private Long claimantTypeId;

    private Long bankTypeId;

    private String accountNumber;

    private String accountHolderName;

    private String ifscOrRoutingCode;

    private String isDefaultBank;

    private String remarks;

    private String verifiedBy;

    private Timestamp verifiedAt;

    private String isActive;

    private String createdBy;

    private String updatedBy;
}
