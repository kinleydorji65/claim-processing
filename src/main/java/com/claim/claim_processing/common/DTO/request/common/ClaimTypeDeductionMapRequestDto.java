package com.claim.claim_processing.common.DTO.request.common;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimTypeDeductionMapRequestDto {

    private Long id;

    private Long claimTypeId;

    private Long deductionTypeId;

    private Character isAllowed;

    private Integer displayOrder;

    private String remarks;

    private String isActive;

}