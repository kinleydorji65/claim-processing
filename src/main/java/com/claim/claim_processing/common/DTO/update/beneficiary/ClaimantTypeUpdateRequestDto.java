package com.claim.claim_processing.common.DTO.update.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimantTypeUpdateRequestDto {

    private String name;
    private String description;
    private Integer displayOrder;
    private String isActive;
}