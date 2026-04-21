package com.claim.claim_processing.common.DTO.request.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimantTypeCreateRequestDto {

    private String code;
    private String name;
    private String description;
    private Integer displayOrder;
}