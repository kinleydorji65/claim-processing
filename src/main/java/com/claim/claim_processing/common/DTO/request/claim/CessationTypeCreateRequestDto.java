package com.claim.claim_processing.common.DTO.request.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CessationTypeCreateRequestDto {

    private String code;
    private String name;
}
