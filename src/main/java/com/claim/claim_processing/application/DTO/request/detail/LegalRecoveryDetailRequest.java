package com.claim.claim_processing.application.DTO.request.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalRecoveryDetailRequest {
    
    private Long id;
    private Long claimApplicationId;
    
    private Long claimDetailId;
    private String judgementNumber;
    private Long payeeTypeId;
    
    private LocalDate judgementDate;
    private Long dzongkhagId;
    private String convictedOrder;
    private String isConvicted;
    private String payToMember;
    private String createdBy;
    private String updatedBy;
}
