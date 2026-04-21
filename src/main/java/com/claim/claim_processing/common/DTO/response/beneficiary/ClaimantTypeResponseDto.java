package com.claim.claim_processing.common.DTO.response.beneficiary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimantTypeResponseDto {

    private Long id;
    private String code;
    private String name;
}