package com.claim.claim_processing.application.DTO.request.detail;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    private String reason;
    private String createdBy;
    private String updatedBy;
}
