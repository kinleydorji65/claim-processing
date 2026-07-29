package com.claim.claim_processing.application.DTO.response.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalRecoveryResponseDto {
    
    private Long id;
    private Long claimApplicationId;
    private Long claimDetailId;
    private String claimApplicationNumber;  // Optional: include for context
    private String judgementNumber;
    private Long payeeTypeId;
    private String payeeTypeName;  // Optional: include payee type name
    private LocalDate judgementDate;
    private Long dzongkhagId;
    private String dzongkhagName;
    private String convictedOrder;
    private String isConvicted;
    private String payToMember;
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;
}